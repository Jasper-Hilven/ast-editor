(ns nodes.relations
  (:require [nodes.struct :refer :all]
            [nodes.constraints :refer :all]))

;;Types
(defn is-my-or-parent-type [struct node type-to-check]
  (is-me-or-parent (get-node-type struct node) type-to-check))
(defn is-type-scope-container [struct node]
  (is-my-or-parent-type struct node :scopecontainer))
(defn is-type-function-def [struct node]
  (is-my-or-parent-type struct node :funcdef))
(defn is-type-s-expr [struct node]
  (is-my-or-parent-type struct node :sexpr))
(defn is-type-function-call [struct node]
  (is-my-or-parent-type struct node :funccall))
(defn is-type-namespace [struct node]
  (is-my-or-parent-type struct node :namespace))

;; FUNCCALL <=> sexpr
(defn get-parameter-usage [struct sexpr]
  (get-node-property struct sexpr :used-as-parameter))
(defn get-parameter-map [struct funccall]
  (get-node-property struct funccall :parameter-map))

;; SCOPECONTAINER <=> sexpr
(defn get-scope-parent [struct sexpr]
  (get-node-property struct sexpr :parent-scope))
(defn has-scope-parent [struct node]
  (nil? (get-scope-parent struct node)))
(defn get-scope-children [struct scopecontainer]
  (get-node-property struct scopecontainer :scope-children))

;; funcdef <=> parameter
(defn get-function-parameters [struct funcdef]
  (get-node-property struct funcdef :parameters))
(defn get-function-of-parameter [struct parameter]
  (get-node-property struct parameter :function))

;; funcdef <=> result
(defn get-result [struct funcdef]
  (get-node-property struct funcdef :result))
(defn get-functions-which-use-me-as-result [struct result-sexpr]
  (get-node-property struct result-sexpr :function-result))

;; parent-namespace <=> child-namespace
(defn get-namespace-children [struct parent-namespace]
  (get-node-property struct parent-namespace :namespace-children))
(defn get-parent-namespace [struct child-namespace]
  (get-node-property struct child-namespace :parent-namespace))
(defn get-name [struct node]
  (get-node-property struct node :name))

(defn find-namespaces [state]
  (filter #(= (get-node-property state % :type) :namespace)
          (get-all-nodes-keys state)))