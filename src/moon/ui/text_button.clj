(ns moon.ui.text-button
  (:require [moon.ui.actor :as actor]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.event :as event]
            [gdl.ui.stage :as stage]
            [gdl.ui.text-button :as text-button]))

(defn create
  [{:keys [text
           on-clicked
           skin]}]
  (doto (text-button/create (str text) skin)
    (actor/add-listener!
     (change-listener/create
      (fn [event actor]
        (on-clicked actor (stage/ctx (event/stage event))))))))
