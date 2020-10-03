(ns ui.text-render
  (:require [clojure.pprint :refer [cl-format]]
            [environment.start-environment :as env]
            [nodes.struct :as struct]
            [nodes.relations :as relations]))

(defn get-full-namespace-name [state namespaceId]
  (let [parentNode (relations/get-parent-namespace state namespaceId)
        childName (relations/get-name state namespaceId)
        parent-prefix (if parentNode (str (get-full-namespace-name state parentNode) ".") "")]
    (str parent-prefix childName)))

(defn findFunctionsForNamespace [state namespaceId]
  (filter #(relations/is-type-function-def state %)
          (into [] (relations/get-scope-children state namespaceId))))

(defn getCorrectInformationForParameter [state, parameterId]
  (let [parameterName (struct/get-node-property state parameterId :name)
        value (struct/get-node-property state parameterId :value)]
    (if parameterName
      (str parameterName)
      (if (string? value)
        (str "\"" value "\"(const)")
        (str value "(const)"))
      )))

(defn findParametersForFunction [state functionId]
  (reduce #(str %1 "," %2)
          (map #(str (getCorrectInformationForParameter state %))
               (into [] (struct/get-node-property state functionId :parameters)))))

(struct/get-all-nodes env/start-environment)

(defn parseConstant [state constantId]
  (let [name (struct/get-node-property state constantId :name)
        value (struct/get-node-property state constantId :value)]
    (if name (str name ": " value))))

(defn getConstantsForFunction [state functionId]
  (let [scopeChildren (into [] (struct/get-node-property state functionId :scope-children))]
    (reduce #(str %1 "\n\t\t" %2)
            ""
            (map #(parseConstant state %)
                 (filter #(= (struct/get-node-property state % :type) :constant) scopeChildren)))))

(defn renderFunctionContent [state functionId]
  (let [functionName (struct/get-node-property state functionId :name)
        functionParameters (findParametersForFunction state functionId)]
    (str " function " functionName "(" functionParameters ")"
         "\t\t" (getConstantsForFunction state functionId))))



(defn getFunctionsAndTheirParameters [state namespace]
  (reduce #(str %1 "\n\t" %2)
          (map #(renderFunctionContent state %)
               (findFunctionsForNamespace state namespace))))


(defn render-function-def [struct function])
(defn render-s-expr [struct s-expr parent-container])
(defn sort-namespaces-by-name [struct namespaces]
  (sort-by #(relations/get-name struct %) namespaces))
(defn render-scope-children [struct scope-container]
  (let [children (relations/get-scope-children struct scope-container)
        named-children (filter #(relations/has-name struct %) children)
        unnamed-children (filter #(not (relations/has-name struct %)) children)
        ] {:children         children
           :unnamed-children unnamed-children
           :named-children   named-children}))

(defn render-namespace [struct namespace]
  {:type             :namespace
   :name             (relations/get-name struct namespace)
   :full-name        (get-full-namespace-name struct namespace)
   :child-namespaces (map #(render-namespace struct %) (relations/get-namespace-children struct namespace))
   :scope-children   (render-scope-children struct namespace)})
(defn render-namespaces [struct]
  (let [namespaces (relations/find-namespaces struct)
        root-namespaces (filter #(nil? (relations/get-parent-namespace struct %)) namespaces)
        sorted-root-namespaces (sort-namespaces-by-name struct root-namespaces)]
    (map #(render-namespace struct %) sorted-root-namespaces)))

(defn pprint [state]
  (let [lines (map #(str " namespace " (get-full-namespace-name state %) "\n\t" (getFunctionsAndTheirParameters state %))
                   (relations/find-namespaces state))]
    (reduce #(str %1 "\n\n" %2)
            lines)))