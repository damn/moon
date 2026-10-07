(ns editor.listener
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [editor.ui :refer [main-window]]
            [moon.db :as db]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx ApplicationListener Audio Files Gdx)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn listener []
  (let [state (atom nil)
        play-sound! (fn [sound-name]
                      (let [audio (:audio @state)]
                        (assert (contains? audio sound-name) (str sound-name))
                        (.play ^Sound (get audio sound-name))))]
    (reify ApplicationListener
      (create [_]
        (let [batch (SpriteBatch.)
              skin (Skin. (.internal Gdx/files "skin/uiskin.json"))
              _ (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
              stage (Stage. (FitViewport. (float 1440) (float 900)) batch)
              audio (into {}
                          (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
                                :let [path (format "sounds/%s.wav" sound-name)]]
                            [sound-name
                             (.newSound ^Audio Gdx/audio (.internal ^Files Gdx/files path))]))
              db (db/create)
              textures (textures/create Gdx/files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})]
          (reset! state {:audio audio
                         :batch batch
                         :db db
                         :play-sound! play-sound!
                         :skin skin
                         :sound-names (keys audio)
                         :stage stage
                         :textures textures})
          (.setInputProcessor Gdx/input stage)
          (.addActor stage (main-window state))))
      (dispose [_]
        (let [{:keys [audio batch skin textures]} @state]
          (run! Disposable/.dispose (vals audio))
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
