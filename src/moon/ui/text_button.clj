(ns moon.ui.text-button
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.event :as event]
            [gdl.ui.change-listener :as change-listener]
            [moon.ui :as ui]
            [moon.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextButton)))

(defn create
  ([text on-clicked]
   (create {:text text
            :on-clicked on-clicked}))
  ([{:keys [text
            on-clicked]
     :as opts}]
   (let [actor (doto (TextButton. (str text) ui/skin)
                 (actor/add-listener!
                  (change-listener/create
                    (fn [event actor]
                      (on-clicked actor (stage/ctx (event/stage event))))))
                 (table/set-opts! opts))]
     (when-let [tooltip (:tooltip opts)]
       (tooltip/add! actor tooltip))
     actor)))
