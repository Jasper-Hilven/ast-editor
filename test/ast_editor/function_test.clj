(ns ast-editor.function_test
  (:require [clojure.test :refer :all]
            [ast-editor.struct :refer :all]
            [ast-editor.function :refer :all]))

(def function-with-parameter
  (-> (get-empty-struct)
      (add-function-node) :struct
      (add-function-parameter-intern 0) :struct))

(deftest test-namespacing
  (testing "should be able to use functions"
    (is (= 1 1))
    (is (= (get-function-parameters function-with-parameter 0)))))

(run-all-tests)

