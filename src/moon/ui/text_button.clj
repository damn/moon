(ns moon.ui.text-button
  (:require [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.utils.change-listener :as change-listener]
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
