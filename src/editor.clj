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
  (let [{:keys [schemas skin stage]} @state
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
                                                                (property-editor-window state (db/get-raw (:db @state) id)))))))))))
      (.row window))
    (.pack window)
    window))

(defn listener []
  (let [state (atom nil)
        sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)]
    (reify ApplicationListener
      (create [_]
        (let [batch (SpriteBatch.)
              skin (Skin. (.internal Gdx/files "skin/uiskin.json"))
              _ (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
              stage (Stage. (FitViewport. (float 1440) (float 900)) batch)
              audio (into {}
                          (for [path sound-paths]
                            [path (.newSound Gdx/audio (.internal Gdx/files path))]))
              schemas (-> "schema.edn" io/resource slurp edn/read-string)
              properties-file (io/resource "properties.edn")
              db (db/create schemas)
              textures (textures/create Gdx/files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})]
          (reset! state {:audio audio
                         :batch batch
                         :db db
                         :properties-file properties-file
                         :schemas schemas
                         :play-sound! (fn [sound-name]
                                        (assert (contains? (:audio @state) sound-name) (str sound-name))
                                        (.play ^Sound (get (:audio @state) sound-name)))
                         :skin skin
                         :sound-names (keys audio)
                         :stage stage
                         :textures textures})
          (.setInputProcessor Gdx/input stage)
          (.addActor stage (main-window state))))
      (dispose [_]
        (let [{:keys [audio batch skin textures]} @state]
          (run! Sound/.dispose (vals audio))
          (Disposable/.dispose batch)
          (Disposable/.dispose skin)
          (run! Disposable/.dispose (vals textures))))
      (render [_]
        (let [{:keys [stage]} @state]
          (ScreenUtils/clear 0 0 0 0)
          (.act ^Stage stage)
          (.draw ^Stage stage)))
      (resize [_ width height]
        (.update (.getViewport ^Stage (:stage @state)) width height true))
      (pause [_])
      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "!Editor!")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
