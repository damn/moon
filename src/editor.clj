(ns editor
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [editor.ui.overview :refer [property-overview-window]]
            [editor.ui.property :refer [property-editor-window]]
            [moon.db :as db]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx ApplicationListener Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn main-window [state]
  (let [{:keys [schemas skin stage]} state
        window (Window. "Edit" skin)]
    (doseq [property-type (sort (filter #(= "properties" (namespace %)) (keys schemas)))]
      (.add window (doto (TextButton. (str/capitalize (name property-type)) skin)
                     (.addListener (proxy [ChangeListener] []
                                     (changed [event _actor]
                                       (.addActor ^Stage stage
                                                  (property-overview-window
                                                   state
                                                   property-type
                                                   (fn [_actor id]
                                                     (.addActor ^Stage stage
                                                                (property-editor-window state (db/get-raw @(:db state) id)))))))))))
      (.row window))
    (.pack window)
    window))

(defn listener []
  (let [sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
        schemas (-> "schema.edn" io/resource slurp edn/read-string)
        properties-file (io/resource "properties.edn")
        audio (atom nil)
        batch (atom nil)
        db (atom nil)
        rebuild-editor-window! (atom nil)
        skin (atom nil)
        stage (atom nil)
        textures (atom nil)
        play-sound! (fn [sound-name]
                      (let [sounds @audio]
                        (assert (contains? sounds sound-name) (str sound-name))
                        (.play ^Sound (get sounds sound-name))))]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! skin (Skin. (.internal Gdx/files "skin/uiskin.json")))
        (set! (.markupEnabled (.getData (.getFont ^Skin @skin "default-font"))) true)
        (reset! stage (Stage. (FitViewport. (float 1440) (float 900)) @batch))
        (reset! audio (into {}
                            (for [path sound-paths]
                              [path (.newSound Gdx/audio (.internal Gdx/files path))])))
        (reset! db (db/create schemas))
        (reset! textures (textures/create Gdx/files {:folder "resources/"
                                                     :extensions #{"png" "bmp"}}))
        (.setInputProcessor Gdx/input @stage)
        (.addActor ^Stage @stage
                   (main-window {:db db
                                 :play-sound! play-sound!
                                 :properties-file properties-file
                                 :rebuild-editor-window! rebuild-editor-window!
                                 :schemas schemas
                                 :skin @skin
                                 :sound-names (keys @audio)
                                 :stage @stage
                                 :textures @textures})))

      (dispose [_]
        (run! Sound/.dispose (vals @audio))
        (Disposable/.dispose @batch)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures)))

      (render [_]
        (ScreenUtils/clear 0 0 0 0)
        (.act ^Stage @stage)
        (.draw ^Stage @stage))

      (resize [_ width height]
        (.update (.getViewport ^Stage @stage) width height true))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "!Editor!")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
