; TODO thies doesnt work anymore
; =? Write tests first ?
; => editor separate/etc/different starters/manual test checkboxes program ?
; project program read code, show LoC, generate docs button, generate ns graph button, start button, etc.
; shows commit/branch/etc. ?
(ns moon.levelgen
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table]
            [moon.ui.table :as table]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.create.textures]
            [moon.db :as db]
            [moon.db-impl :as db-impl]
            [moon.graphics.camera :as camera]
            [moon.tiled-map-renderer :as tiled-map-renderer]
            [moon.world-fns.creature-tiles])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx
                             Input$Keys)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics Color
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (com.badlogic.gdx.utils ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (moon Stage)))

(def initial-level-fn "world_fns/uf_caves.edn")

(def level-fns
  ["world_fns/vampire.edn"
   "world_fns/uf_caves.edn"
   "world_fns/modules.edn"])

(defn- show-whole-map! [{:keys [ctx/camera
                                ctx/tiled-map]}]
  (camera/set-position! camera
                        [(/ (.get (.getProperties tiled-map) "width") 2)
                         (/ (.get (.getProperties tiled-map) "height") 2)])
  (camera/set-zoom! camera
                    (camera/calculate-zoom camera
                                           :left [0 0]
                                           :top [0 (.get (.getProperties tiled-map) "height")]
                                           :right [(.get (.getProperties tiled-map) "width") 0]
                                           :bottom [0 0])))

(def tile-size 48)

(defn- generate-level [{:keys [ctx/db
                               ctx/textures
                               ctx/tiled-map] :as ctx} level-fn]
  (when tiled-map
    (.dispose tiled-map))
  (let [level (let [[f params] (-> level-fn
                                   io/resource
                                   slurp
                                   edn/read-string)]
                ((requiring-resolve f)
                 (assoc params
                        :level/creature-properties (moon.world-fns.creature-tiles/prepare
                                                    (db/all-raw db :properties/creatures)
                                                    (fn [{:keys [image/file image/bounds]}]
                                                      (assert file)
                                                      (assert (contains? textures file))
                                                      (let [texture (get textures file)]
                                                        (if-let [[x y w h] bounds]
                                                          (TextureRegion. texture x y w h)
                                                          (TextureRegion. texture)))))
                        :textures textures)))
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (.setVisible (.get (.getLayers tiled-map) "creatures") true)
    (show-whole-map! ctx)
    ctx))

(defn- edit-window [skin]
  (let [window (Window. "Edit" skin)]
    (table/add-rows! window
                     (for [level-fn level-fns
                           :let [on-clicked (fn [actor ctx]
                                              (let [stage (.getStage actor)
                                                    new-ctx (generate-level ctx level-fn)]
                                                (set! (.ctx stage) new-ctx)))]]
                       [{:actor (doto (TextButton. (str "Generate " level-fn) skin)
                                  (.addListener
                                   (proxy [ChangeListener] []
                                     (changed [event actor]
                                       (on-clicked actor (.ctx (Event/.getStage event)))))))}]))
    (.pack window)
    window))

(defn create!
  [{:keys [ctx/files
           ctx/graphics
           ctx/input]}]
  (let [skin (Skin. (.internal Gdx/files "uiskin.json")) ; TODO dispose
        ui-viewport (FitViewport. 1440 900 (OrthographicCamera.))
        sprite-batch (SpriteBatch.)
        stage (Stage. ui-viewport sprite-batch)
        _  (.setInputProcessor input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx {:ctx/stage stage
             :ctx/files files}
        ctx (-> ctx
                (moon.create.textures/step {:folder "resources/"
                                            :extensions #{"png" "bmp"}})
                (assoc :ctx/db (db-impl/create)))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (FitViewport. world-width
                                       world-height
                                       (doto (OrthographicCamera.)
                                         (.setToOrtho false world-width world-height))))
        ctx (assoc ctx
                   :ctx/input input
                   :ctx/world-viewport world-viewport
                   :ctx/ui-viewport ui-viewport
                   :ctx/camera (.getCamera world-viewport)
                   :ctx/color-setter (constantly [1 1 1 1])
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/sprite-batch sprite-batch
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level ctx initial-level-fn)]
    (.addActor (:ctx/stage ctx) (edit-window skin))
    ctx))

(defn dispose!
  [{:keys [ctx/sprite-batch
           ctx/tiled-map ]}]
  ; TODO dispose skin?
  (.dispose sprite-batch)
  (.dispose tiled-map))

(defn- draw-tiled-map! [{:keys [ctx/sprite-batch
                                ctx/color-setter
                                ctx/tiled-map
                                ctx/tiled-map-renderer
                                ctx/world-unit-scale
                                ctx/world-viewport]}]
  (tiled-map-renderer/draw! sprite-batch
                            world-unit-scale
                            (.getCamera world-viewport)
                            tiled-map
                            color-setter))

(defn- camera-movement-controls! [{:keys [ctx/input
                                          ctx/camera
                                          ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (camera/set-position! camera
                                               (update (camera/position camera)
                                                       idx
                                                       #(f % camera-movement-speed))))]
    (if (.isKeyPressed input Input$Keys/LEFT)  (apply-position 0 -))
    (if (.isKeyPressed input Input$Keys/RIGHT) (apply-position 0 +))
    (if (.isKeyPressed input Input$Keys/UP)    (apply-position 1 +))
    (if (.isKeyPressed input Input$Keys/DOWN)  (apply-position 1 -))))

(defn- camera-zoom-controls! [{:keys [ctx/input
                                      ctx/camera
                                      ctx/zoom-speed]}]
  (when (.isKeyPressed input Input$Keys/MINUS)  (camera/inc-zoom! camera zoom-speed))
  (when (.isKeyPressed input Input$Keys/EQUALS) (camera/inc-zoom! camera (- zoom-speed))))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (.ctx stage)]
              new-ctx
              ctx)]
    (ScreenUtils/clear Color/BLACK)
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (set! (.ctx stage) ctx)
    (.act stage)
    (.draw stage)
    (.ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (.update ui-viewport    width height true)
  (.update world-viewport width height false))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (create! {:ctx/files    Gdx/files
                                                  :ctx/graphics Gdx/graphics
                                                  :ctx/input    Gdx/input})))
                        (dispose [_]
                          (dispose! @state))
                        (render [_]
                          (swap! state render!))
                        (resize [_ width height]
                          (resize! @state width height))
                        (pause [_])
                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
