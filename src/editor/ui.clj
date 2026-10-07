(ns editor.ui
  (:require [editor.ui.overview :refer [property-overview-window]]
            [editor.ui.property :refer [property-editor-window]]
            [moon.db :as db]
            [clojure.string :as str])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn main-window [state]
  (let [{:keys [db skin stage]} @state
        window (Window. "Edit" skin)]
    (doseq [property-type (sort (db/property-types db))]
      (.add window (doto (TextButton. (str/capitalize (name property-type)) skin)
                     (.addListener (proxy [ChangeListener] []
                                     (changed [event _actor]
                                       (.addActor ^Stage stage
                                                  (property-overview-window
                                                   state
                                                   property-type
                                                   (fn [_actor id]
                                                     (.addActor ^Stage stage
                                                                (property-editor-window state (db/get-raw (:db @state) id)))))))))))
      (.row window))
    (.pack window)
    window))
