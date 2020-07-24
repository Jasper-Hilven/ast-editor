(ns ast-editor.struct-test
  (:require [clojure.test :refer :all]
            [ast-editor.struct :refer :all]
            [ast-editor.core :refer :all]))

(def increased-by-one (increase-next (get-empty-struct)))
(def one-node-added (:struct (add-node (get-empty-struct) {:name :node})))
(def two-nodes-added (:struct (add-node one-node-added {:name :node2})))
(def all-node-keys-two-nodes (get-all-nodes-keys two-nodes-added))
(def all-nodes-two-nodes (get-all-nodes two-nodes-added))
(def removed-node-from-two-nodes (remove-node two-nodes-added 0))
(def remove-two-nodes-from-two-nodes (remove-node removed-node-from-two-nodes 1))

(deftest struct
  (testing "should be able to do simple operations on node"
    (is (= (:next increased-by-one) 1))
    (is (= (:next one-node-added) 1))
    (is (= (:next two-nodes-added) 2))
    (is (= all-nodes-two-nodes '({:name :node} {:name :node2})))
    (is (= all-node-keys-two-nodes '(0 1)))
    (is (= (:nodes remove-two-nodes-from-two-nodes) {}))
    (is (= (:holes remove-two-nodes-from-two-nodes) [0 1]))))

(run-all-tests)