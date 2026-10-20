import assert from "node:assert/strict";
import test from "node:test";
import { parseSearchFuzzy } from "../app/utils/globalSearch.ts";

test("linguistic matching is the default", () => {
  assert.equal(parseSearchFuzzy(undefined, undefined), true);
  assert.equal(parseSearchFuzzy(undefined, "false"), true);
});

test("old exact bookmarks disable fuzzy matching", () => {
  assert.equal(parseSearchFuzzy(undefined, "true"), false);
  assert.equal(parseSearchFuzzy(undefined, ["true"]), false);
});

test("an explicit fuzzy mode overrides legacy exact bookmarks", () => {
  assert.equal(parseSearchFuzzy("true", "true"), true);
  assert.equal(parseSearchFuzzy("false", "false"), false);
  assert.equal(parseSearchFuzzy(["false"], "true"), false);
  assert.equal(parseSearchFuzzy(["true"], "true"), true);
});
