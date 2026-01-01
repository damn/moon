(ns moon.ui.text-button
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.event :as event]
            [gdl.ui.stage :as stage]
            [gdl.ui.text-button :as text-button]
            [moon.ui :as ui]))

(defn create
  [{:keys [text
           on-clicked]}]
  (doto (text-button/create (str text) ui/skin)
    (actor/add-listener!
     (change-listener/create
      (fn [event actor]
        (on-clicked actor (stage/ctx (event/stage event))))))))
