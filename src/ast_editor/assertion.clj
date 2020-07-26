(ns ast-editor.assertion)
(defn assert-condition [condition] (fn [to-check] (if (condition to-check) to-check (new Exception "Assertion failed"))))
