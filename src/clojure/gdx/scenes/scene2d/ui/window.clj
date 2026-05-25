(ns clojure.gdx.scenes.scene2d.ui.window
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defmethod actor/create :ui/window
  [{:keys [title skin] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (when (:window/modal? opts)
      (.setModal window true))

    (when-let [skin (:window/close-button? opts)]
      (table/add! (.getTitleTable window)
                  {:actor (actor/create
                           {:type :ui/text-button
                            :text "X"
                            :skin skin
                            :actor/listeners {:listener/change (fn [_event _actor]
                                                                 (actor/remove! window))}})}))

    (table/set-opts! window opts)
    window))
