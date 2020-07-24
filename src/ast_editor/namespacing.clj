(ns ast-editor.namespacing
  (:require [ast-editor.struct :refer :all]))

(defn add-namespace-node [struct parent-namespace]
  )
(defn assert-condition [condition] (fn [to-check] (if (condition to-check) to-check (new Exception "Assertion failed"))))
(defn is-namespace? [node] (= :namespace (:type node)))
(defn assert-is-namespace [to-assert] ((assert-condition is-namespace?) to-assert))
(defn get-namespace-keys [namespace] (filter string? (keys namespace)))
(defn get-namespace-children [namespace] (map namespace (get-namespace-keys namespace)))
(defn get-all-namespace-keys [structure] (filter #(is-namespace? (structure %)) (get-all-nodes-keys structure)))
