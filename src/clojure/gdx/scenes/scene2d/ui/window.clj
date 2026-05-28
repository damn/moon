(ns clojure.gdx.scenes.scene2d.ui.window
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn create
  [{:keys [title skin] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (when (:window/modal? opts)
      (.setModal window true))

    (when-let [skin (:window/close-button? opts)]
      (table/add! (.getTitleTable window)
                  {:actor (text-button/create
                           {:text "X"
                            :skin skin
                            :actor/listeners {:listener/change (fn [_event _actor]
                                                                 (actor/remove! window))}})}))

    (table/set-opts! window opts)
    window))
