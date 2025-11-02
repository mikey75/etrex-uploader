# Strava Client Test Suite & Emulator
To fully, reliably and offline test `StravaClient` we provide a custom Strava API emulator (not all endpoints from API, only those used by `etrex-uploader`)

## Structure

- **StravaClientTest.java**  
  Main test class covering all Strava client functionality used by the app (fetching, updating, uploading activities, token logic, error handling, etc).

- **StravaEmu.java**  
  SpringBoot test-scoped web api application that mimics the Strava API, serving responses from static JSON files being the representation of real strava data.

- **src/test/resources/strava-emulator/**  
  Test resource directory with realistic Strava API responses, organized by resource type.

No external credentials or API keys are required; the emulator is used automatically, and the real keys/credentials 
if you happen to have them inside work dir are not touched in the tests/emulator. No real strava API calls to real endpoints.

## Extending the Emulator & Tests

- To add a new test scenario, place the required JSON response in the appropriate subfolder of `src/test/resources/strava-emulator`.
- Update, extend, or add new Spring controller to serve new endpoints or handle new edge cases.
- Add or modify test methods in `StravaClientTest.java` as needed.

## Keeping Up-to-Date with Strava API Changes

- If the Strava API changes (new fields, endpoints, error codes), update the emulator resources and logic to match.
- Periodically verify emulator responses against the real API (see Strava API docs).
- Document any emulator limitations in this README.

## Troubleshooting

- If tests fail with file-not-found or port-in-use errors, ensure you have a clean checkout and no other test runs are active.
- The emulator will print classic springboot logs to the test output.

---

*For questions, contact the project maintainer or open an issue.*