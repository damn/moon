(ns editor.listener
  (:require [audio.create :refer [create-audio!]]
            [audio.dispose :as audio.dispose]
            [audio.play :refer [play-sound!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [editor.ui :refer [main-window]]
            [files.create :refer [create-sound-file-handles!]]
            [moon.db :as db]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx ApplicationListener Gdx)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn listener []
  (let [state (atom nil)
        sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)]
    (reify ApplicationListener
      (create [_]
        (let [batch (SpriteBatch.)
              skin (Skin. (.internal Gdx/files "skin/uiskin.json"))
              _ (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
              stage (Stage. (FitViewport. (float 1440) (float 900)) batch)
              audio (create-audio! Gdx/audio
                                   (create-sound-file-handles! Gdx/files sound-paths))
              schemas (-> "schema.edn" io/resource slurp edn/read-string)
              db (db/create schemas)
              textures (textures/create Gdx/files {:folder "resources/"
                                                   :extensions #{"png" "bmp"}})]
          (reset! state {:audio audio
                         :batch batch
                         :db db
                         :schemas schemas
                         :play-sound! #(play-sound! (:audio @state) %)
                         :skin skin
                         :sound-names (keys audio)
                         :stage stage
                         :textures textures})
          (.setInputProcessor Gdx/input stage)
          (.addActor stage (main-window state))))
      (dispose [_]
        (let [{:keys [audio batch skin textures]} @state]
          (audio.dispose/dispose! audio)
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
