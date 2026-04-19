(ns clojure.gdx.scene2d.ui.window
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn set-opts! [window opts]
  (when (:window/modal? opts)
    (Window/.setModal window true))

  (when-let [skin (:window/close-button? opts)]
    (table/add! (Window/.getTitleTable window)
                {:actor (actor/create
                         {:type :ui/text-button
                          :text "X"
                          :skin skin
                          :actor/listeners {:listener/change (fn [_event _actor]
                                                               (actor/remove! window))}})}))

  (table/set-opts! window opts))

(defn create
  [{:keys [title skin] :as opts}]
  (doto (Window. ^String title ^Skin skin)
    (set-opts! opts)))
