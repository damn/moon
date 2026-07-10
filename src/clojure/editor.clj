(ns clojure.editor
  (:require [clojure.ctx :as ctx]
            [clojure.db :as db]
            [clojure.editor.create-widget-default]
            [clojure.editor.create-widget-s-animation]
            [clojure.editor.create-widget-s-boolean]
            [clojure.editor.create-widget-s-enum]
            [clojure.editor.create-widget-s-image]
            [clojure.editor.create-widget-s-map]
            [clojure.editor.create-widget-s-number]
            [clojure.editor.create-widget-s-one-to-many]
            [clojure.editor.create-widget-s-one-to-one]
            [clojure.editor.create-widget-s-sound]
            [clojure.editor.create-widget-s-string]
            [clojure.editor.create-widget-s-val-max]
            [clojure.editor.main-window :as main-window]
            [clojure.files.create-textures :as create-textures]
            [clojure.malli-form-register-methods]
            [clojure.scene2d-stage :as scene2d-stage]
            [clojure.set-ctx :as set-ctx]
            [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as ui-skin]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(def state (atom nil))

(defn- input-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/input (application/getInput app)))

(defn- audio-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (application/getAudio app)))

(defn- files-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/files (application/getFiles app)))

(defn- graphics-f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/graphics (application/getGraphics app)))

(defn- batch-f [ctx]
  (assoc ctx :ctx/batch (sprite-batch/new)))

(defn- skin-f [{:keys [ctx/files] :as ctx}]
  (let [skin (ui-skin/new (files/internal files "skin/uiskin.json"))]
    (-> skin
        (ui-skin/getFont "default-font")
        bitmap-font/getData
        (bitmap-font-data/set-markupEnabled true))
    (assoc ctx :ctx/skin skin)))

(defn- db-f [ctx]
  (assoc ctx :ctx/db (db/create)))

(defn- stage-f [{:keys [ctx/input
                        ctx/batch] :as ctx}]
  (let [stage* (scene2d-stage/create (fit-viewport/new 1440 900) batch)]
    (input/setInputProcessor input stage*)
    (let [ctx (assoc ctx :ctx/stage stage*)]
      (stage/addActor (:ctx/stage ctx) (main-window/f ctx))
      ctx)))

(defn- textures-f [{:keys [ctx/files] :as ctx}]
  (assoc ctx :ctx/textures (create-textures/f files {:folder "resources/"
                                                      :extensions #{"png" "bmp"}})))

(defn create [application]
  (-> {:ctx/app application}
      input-f
      audio-f
      files-f
      graphics-f
      batch-f
      skin-f
      db-f
      stage-f
      textures-f))

(defn dispose [{:keys [ctx/skin
                       ctx/batch
                       ctx/textures]}]
  (disposable/dispose batch)
  (disposable/dispose skin)
  (run! disposable/dispose (vals textures)))

(defn render [{:keys [ctx/stage]
               :as ctx}]
  (let [ctx (ctx/clear ctx)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act stage)
    (stage/draw stage)
    (:stage/ctx stage)))

(defn resize [{:keys [ctx/stage]} width height]
  (viewport/update (:stage/viewport stage) width height true))

(defn -main []
  (lwjgl3-application/create {:create! (fn [app]
                                         (reset! state (create app)))
                              :dispose! (fn []
                                          (dispose @state))
                              :render! (fn []
                                         (swap! state render))
                              :resize! (fn [width height]
                                         (resize @state width height))}
                             {:config/set-title "!Editor!"
                              :config/set-windowed-mode {:width 1440
                                                         :height 900}
                              :config/set-foreground-fps 60}))
