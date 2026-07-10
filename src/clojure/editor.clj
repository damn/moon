(ns clojure.editor
  (:require [clojure.ctx :as ctx]
            [clojure.editor.audio :as audio]
            [clojure.editor.batch :as batch]
            [clojure.editor.create-widget-register-methods]
            [clojure.editor.db :as db]
            [clojure.editor.files :as files]
            [clojure.editor.graphics :as graphics]
            [clojure.editor.input :as input]
            [clojure.editor.skin :as skin]
            [clojure.editor.stage :as editor-stage]
            [clojure.editor.textures :as textures]
            [clojure.malli-form-register-methods]
            [clojure.set-ctx :as set-ctx]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(def state (atom nil))

(defn create [application]
  (-> {:ctx/app application}
      input/f
      audio/f
      files/f
      graphics/f
      batch/f
      skin/f
      db/f
      editor-stage/f
      textures/f))

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
