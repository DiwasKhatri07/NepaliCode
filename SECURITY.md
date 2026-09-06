# Security policy

## Supported versions

The latest default branch and the latest published release receive security review as capacity allows.

## Reporting a vulnerability

Please do not publish exploitable details in a public issue. Contact the maintainer through [@diwaskhatri](https://github.com/diwaskhatri) with a description, affected version, reproduction steps, and suggested mitigation. Remove API keys, access tokens, private URLs, and personal data from all reports.

## Secrets and releases

Never commit `.env` files, Gemini keys, Firebase credentials, keystores, passwords, or release signing material. Public release artifacts should be signed with a protected production key managed outside the repository. If a secret is exposed, rotate it immediately and open a private security report.
