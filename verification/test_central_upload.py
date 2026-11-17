import base64
import importlib.util
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import urllib.parse
import urllib.error

spec = importlib.util.spec_from_file_location("central_upload", Path(__file__).resolve().parent.parent / "scripts/upload-central-bundle.py")
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class FakeResponse:
    def __enter__(self): return self
    def __exit__(self, *args): pass
    def read(self, size): return b"28570f16-da32-4c14-bd2e-c1acc0782365"


class CentralUploadTest(unittest.TestCase):
    def test_upload_is_user_managed_and_contains_the_bundle(self):
        with tempfile.TemporaryDirectory() as tmp:
            bundle = Path(tmp) / "bundle.zip"
            bundle.write_bytes(b"bundle contents")
            with patch.object(module.urllib.request, "urlopen", return_value=FakeResponse()) as send:
                deployment = module.upload(bundle, "1.0.0", "user", "password")
            request = send.call_args.args[0]
            self.assertEqual("28570f16-da32-4c14-bd2e-c1acc0782365", deployment)
            self.assertEqual("USER_MANAGED", urllib.parse.parse_qs(urllib.parse.urlparse(request.full_url).query)["publishingType"][0])
            self.assertEqual("Bearer " + base64.b64encode(b"user:password").decode(), request.get_header("Authorization"))
            self.assertIn(b"bundle contents", request.data)

    def test_missing_credentials_prevent_network_request(self):
        with patch.object(module.urllib.request, "urlopen") as send:
            with self.assertRaises(ValueError):
                module.upload(Path("unused"), "1.0.0", "", "")
            send.assert_not_called()

    def test_http_error_does_not_echo_credentials_or_response(self):
        with tempfile.TemporaryDirectory() as tmp:
            bundle = Path(tmp) / "bundle.zip"
            bundle.write_bytes(b"bundle")
            error = urllib.error.HTTPError("https://example.invalid", 401, "secret response", {}, None)
            with patch.object(module.urllib.request, "urlopen", side_effect=error):
                with self.assertRaises(RuntimeError) as caught:
                    module.upload(bundle, "1.0.0", "user", "secret-password")
            self.assertNotIn("secret", str(caught.exception))

    def test_timeout_requires_checking_the_portal_before_retry(self):
        with tempfile.TemporaryDirectory() as tmp:
            bundle = Path(tmp) / "bundle.zip"
            bundle.write_bytes(b"bundle")
            with patch.object(module.urllib.request, "urlopen", side_effect=TimeoutError("timeout")):
                with self.assertRaisesRegex(RuntimeError, "existing deployment before retrying"):
                    module.upload(bundle, "1.0.0", "user", "password")


if __name__ == "__main__":
    unittest.main()
