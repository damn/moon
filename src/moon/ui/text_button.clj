(ns moon.ui.text-button
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.event :as event]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.text-button :as text-button]
            [moon.ui :as ui]
            [gdl.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.tooltip :as tooltip]))

(defn create
  ([text on-clicked]
   (create {:text text
            :on-clicked on-clicked}))
  ([{:keys [text
            on-clicked]
     :as opts}]
   (let [actor (doto (text-button/create (str text) ui/skin)
                 (actor/add-listener!
                  (change-listener/create
                    (fn [event actor]
                      (on-clicked actor (stage/ctx (event/stage event))))))
                 (table/set-opts! opts))]
     (when-let [tooltip (:tooltip opts)]
       (tooltip/add! actor tooltip))
     actor)))
