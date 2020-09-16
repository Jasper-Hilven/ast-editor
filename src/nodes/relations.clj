(ns nodes.relations
  (:require [nodes.struct :refer :all]
            [nodes.constraints :refer :all]))

(defn get-scope-parent [struct node]
  (get-node-property struct node :parent-scope))
(defn has-scope-parent [struct node]
  (nil? (get-scope-parent struct node)))
(defn is-my-or-parent-type [struct node type-to-check]
  (is-me-or-parent (get-node-type struct node) type-to-check))
(defn is-type-scope-container [struct node]
  (is-my-or-parent-type struct node :scopecontainer))
(defn is-type-function-def [struct node]
  (is-my-or-parent-type struct node :funcdef))
(defn is-type-function-call [struct node]
  (is-my-or-parent-type struct node :funccall))

(defn has-function-that-it-calls [struct function-call]
  (some? (get-node-property struct function-call :calls)))
(defn has-no-function-that-it-calls [struct function-call]
  (nil? (get-node-property struct function-call :calls)))