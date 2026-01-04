(ns moon.actor-create-fns.hp-mana-bar
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.stage :as stage]
            [moon.application.create.ui.hp-mana-bar-config :as hp-mana-bar-config]
            [moon.graphics :as graphics]))

(defn- create-hp-mana-bar* [create-draws]
  {:type :actor/actor
   :act (fn [_this _delta])
   :draw (fn [actor _batch _parent-alpha]
           (when-let [stage (actor/stage actor)]
             (graphics/draw! (:ctx/graphics (stage/ctx stage))
                             (create-draws (stage/ctx stage)))))})

(defn create [ctx]
  (create-hp-mana-bar* (hp-mana-bar-config/create ctx)))
