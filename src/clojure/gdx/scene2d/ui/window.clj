(ns clojure.gdx.scene2d.ui.window
  (:require [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.ui.actor :as actor]
            [clojure.gdx.scene2d.ui.table :as table]))

(defn- set-opts! [window opts]
  (when (:window/modal? opts)
    (window/set-modal! window true))

  (when-let [skin (:window/close-button? opts)]
    (table/add! (window/title-table window)
                {:actor (actor/create
                         {:type :ui/text-button
                          :text "X"
                          :skin skin
                          :actor/listeners {:listener/change (fn [_event _actor]
                                                               (actor/remove! window))}})}))

  (table/set-opts! window opts))

(defmethod actor/create :ui/window
  [opts]
  (doto (window/create opts)
    (set-opts! opts)))
