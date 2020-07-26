(ns ast-editor.namespacing_test
  (:require [clojure.test :refer :all]
            [ast-editor.struct :refer :all]
            [ast-editor.namespacing :refer :all]))

(def empty-struct (get-empty-struct))
(def added-namespace-node (add-namespace-node empty-struct))
(def child-node (:node-key added-namespace-node))
(def added-second-namespace-node (add-namespace-node (:struct added-namespace-node)))
(def second-namespace-node (:node-key added-second-namespace-node))
(def struct-two-namespace (:struct added-second-namespace-node))
(def namespaces-are-children
  (add-node-to-namespace
    struct-two-namespace
    child-node second-namespace-node "child"))
(def node-with-name (get-node-with-name namespaces-are-children second-namespace-node "child"))
(def namespaces-are-no-longer-children
  (remove-name-from-namespace namespaces-are-children second-namespace-node"child"))

(deftest test-namespacing
  (testing "should be able to namespacing"
    (is (= 1 1))
    (is (= 0 node-with-name))
    (is (= (get-node-namespace namespaces-are-children child-node) second-namespace-node))
    (is (= (get-node-with-name namespaces-are-no-longer-children second-namespace-node "child") nil))))

(run-all-tests)

