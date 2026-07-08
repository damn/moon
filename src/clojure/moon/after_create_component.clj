(ns clojure.moon.after-create-component
  (:require [clojure.moon.after-create-component.after-create-fsm :as after-create-fsm]
            [clojure.moon.after-create-component.after-create-skills :as after-create-skills]
            [clojure.moon.after-create-component.inventory :as inventory]))

(def k->after-create
  {:entity/fsm after-create-fsm/f
   :entity/inventory inventory/f
   :entity/skills after-create-skills/f})

(defn after-create-component
  [ctx eid [k v]]
  (if-let [f (k->after-create k)]
    (f v eid ctx)
    nil))
