(ns environment.start-environment
  (:require [nodes.struct :as st]
            [operations.checked :as c]
            [nodes.relations :as r]
            ))

(def start-environment-simple (c/create-func
                                (c/chain-opp (st/get-empty-struct)
                                             #(c/create-namespace % "root")
                                             #(c/create-namespace % "child" 0)
                                             ;;#(c/create-func % "strconcat2" 0 ["first" "second"])
                                             ) "strconcat" 0 ["first" "second"]))
(def start-environment
  (c/chain-opp (st/get-empty-struct)
               #(c/create-namespace % "root")
               #(c/create-namespace % "demo" 0)
               #(c/create-func % "strconcat2" 0 ["first" "second"])
               #(c/create-func % "hello-world" 1 ["name"])
               #(c/create-function-call % 2 5 [6 6])
               #(c/set-as-function-result % 5 7)))



(filter #(r/is-type-function-def start-environment %) (st/get-all-nodes-keys start-environment))


