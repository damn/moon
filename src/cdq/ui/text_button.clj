(ns cdq.ui.text-button
  (:require [cdq.ui.tooltip :as tooltip]
            [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.utils.change-listener :as change-listener]
            [moon.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create
  ([text on-clicked]
   (create {:text text
            :on-clicked on-clicked}))
  ([{:keys [text
            on-clicked]
     :as opts}]
   (let [actor (doto (text-button/create text)
                 (actor/add-listener!
                  (change-listener/create
                    (fn [event actor]
                      (on-clicked actor (stage/ctx (event/stage event))))))
                 (table/set-opts! opts))]
     (when-let [tooltip (:tooltip opts)]
       (tooltip/add! actor tooltip))
     actor)))
