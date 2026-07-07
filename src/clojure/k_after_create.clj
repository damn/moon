(ns clojure.k-after-create
  (:require [clojure.after-create-fsm :as after-create-fsm]
            [clojure.after-create-skills :as after-create-skills]
            [clojure.inventory :as inventory]))

(def k->after-create
  {:entity/fsm after-create-fsm/f
   :entity/inventory inventory/f
   :entity/skills after-create-skills/f})
