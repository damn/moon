; DEsign is separateing something into simple parts
; only simple allowed
(ns moon.application
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.audio :as audio]
            [moon.ctx :as ctx]
            [moon.entity.state-impl]                        ; AHA !
            [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.world :as world])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.utils Disposable))
  (:gen-class))

(defn- dispose!
  [{:keys [ctx/audio
           ctx/graphics
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (audio/dispose! audio)
  (graphics/dispose! graphics)
  (ui/dispose! stage)
  (Disposable/.dispose skin)
  (world/dispose! world)
  ctx)

(defn- resize!
  [{:keys [ctx/graphics] :as ctx} width height]
  (graphics/update-ui-viewport! graphics width height)
  (graphics/update-world-vp! graphics width height)
  ctx)

(def state (atom nil))

(defn apply* [[f params]]
  ((requiring-resolve f) params))

(defn lwjgl-app-config [config]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle (:title config))
    (.setWindowedMode (:width (:window config))
                      (:height (:window config)))
    (.setForegroundFPS (:fps config))))

(defn app-listener [{:keys [create! render!]}]
  (let [render! (requiring-resolve render!)]
    (reify ApplicationListener
      (create [_]
        (reset! state ((requiring-resolve create!)
                       Gdx/app
                       (->> "config.edn" io/resource slurp edn/read-string))))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render!))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [{:keys [listener config]} (->> "start.edn"
                                       io/resource
                                       slurp
                                       edn/read-string)]
    (Lwjgl3Application. (apply* listener)
                        (apply* config))))
