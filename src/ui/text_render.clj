(ns ui.text-render
  (:require [fn-fx.fx-dom :as dom]
            [clojure.pprint :refer [cl-format]]
            [environment.start-environment :as env]
            [nodes.struct :as struct]
            [nodes.relations :as relations]

            ))


(defn get-full-namespace-name [state namespaceId]
  (let [parentNode (relations/get-parent-namespace state namespaceId)
        childName (relations/get-name state namespaceId)
        parent-prefix (if parentNode (str (get-full-namespace-name state parentNode) ".") "")]
    (str parent-prefix childName))

  (defn findFunctionsForNamespace [state namespaceId]
    (filter #(relations/is-type-function-def state %)
            (into [] (relations/get-scope-children state namespaceId)))))

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
               (findFunctionsForNamespace state namespace)))
  )


(defn pprint [state]
  (let [lines (map #(str " namespace " (get-full-namespace-name state %) "\n\t" (getFunctionsAndTheirParameters state %))
                   (relations/find-namespaces state))]
    (reduce #(str %1 "\n\n" %2)
            lines)))