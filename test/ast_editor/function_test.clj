(ns ast-editor.function_test
  (:require [clojure.test :refer :all]
            [ast-editor.struct :refer :all]
            [ast-editor.function :refer :all]))


(deftest test-namespacing
  (testing "should be able to use functions"
    (is (= 1 1))))

(run-all-tests)

