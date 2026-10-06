(ns levelgen
  (:require [moon.db :as db]
            [moon.camera :as orthographic-camera]
            [moon.level.modules :as modules]
            [moon.level.tmx :as tmx]
            [moon.level.uf-caves :as uf-caves]
            [moon.textures :as textures]
            [moon.tiled-map :as moon-tiled-map]
            [moon.color :as color])
  (:import (com.badlogic.gdx.maps.tiled TiledMap TiledMapTileLayer)
           (com.badlogic.gdx Application ApplicationListener Files Gdx Input Input$Keys InputProcessor)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin Table TextButton Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Layout)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)))

(def ^:private config
  {:initial-level-fn uf-caves/create
   :level-fns [["Vampire" tmx/vampire]
               ["UF Caves" uf-caves/create]
               ["Modules" modules/create]]
   :ui-viewport-width 1440
   :ui-viewport-height 900
   :world-viewport-width 1440
   :world-viewport-height 900
   :tile-size 48
   :ui-skin-path "skin/uiskin.json"
   :textures-config {:folder "resources/"
                     :extensions #{"png" "bmp"}}
   :zoom-speed 0.1
   :camera-movement-speed 1})

(defn- generate-level
  [db textures ^OrthographicCamera camera level-fn]
  (let [level (level-fn {:level/creature-properties
                         (moon-tiled-map/prepare-creature-tiles
                          (db/all-raw db :properties/creatures)
                          #(textures/texture-region textures %))
                         :textures textures})
        ^TiledMap tiled-map (:tiled-map level)
        props (.getProperties tiled-map)
        width (.get props "width")
        height (.get props "height")]
    (assert tiled-map)
    (.setVisible ^TiledMapTileLayer (.get (.getLayers tiled-map) "creatures")
                 true)
    (let [pos (.position camera)]
      (set! (.x pos) (/ width 2))
      (set! (.y pos) (/ height 2))
      (.update camera))
    (orthographic-camera/zoom-to-rect camera {:left [0 0]
                                              :top [0 height]
                                              :right [width 0]
                                              :bottom [0 0]})
    tiled-map))

(defn create-viewport [world-width world-height]
  (FitViewport. (float world-width)
                (float world-height)
                (doto (OrthographicCamera.)
                  (.setToOrtho false
                               world-width
                               world-height))))

(defn create-skin [file-handle]
  (Skin. ^FileHandle file-handle))

(defn create-stage [batch viewport actor]
  (doto (Stage. viewport batch)
    (.addActor actor)))

(defn text-button ^TextButton [^Skin skin ^String label on-click!]
  (doto (TextButton. label skin)
    (.addListener (proxy [ChangeListener] []
                    (changed [_event _actor]
                      (on-click!))))))

(defn- zoom-in! [^OrthographicCamera camera]
  (set! (.zoom camera) (max 0.1 (+ (.zoom camera) (:zoom-speed config))))
  (.update camera))

(defn- zoom-out! [^OrthographicCamera camera]
  (set! (.zoom camera) (max 0.1 (+ (.zoom camera) (- (:zoom-speed config)))))
  (.update camera))

(defn- move-camera! [^OrthographicCamera camera idx f]
  (let [pos (.position camera)
        [x y] (update [(.x pos) (.y pos) (.z pos)]
                      idx
                      #(f % (:camera-movement-speed config)))]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn- move-left! [camera]
  (move-camera! camera 0 -))

(defn- move-right! [camera]
  (move-camera! camera 0 +))

(defn- move-up! [camera]
  (move-camera! camera 1 +))

(defn- move-down! [camera]
  (move-camera! camera 1 -))

(defn- handle-controls! [camera]
  (doseq [[k f] {Input$Keys/MINUS zoom-in!
                 Input$Keys/EQUALS zoom-out!
                 Input$Keys/LEFT move-left!
                 Input$Keys/RIGHT move-right!
                 Input$Keys/UP move-up!
                 Input$Keys/DOWN move-down!}]
    (when (.isKeyPressed ^Input Gdx/input k)
      (f camera))))

(defn listener
  [{:keys [tile-size
           world-viewport-width
           world-viewport-height
           ui-viewport-width
           ui-viewport-height
           ui-skin-path
           level-fns
           textures-config
           initial-level-fn]}]
  (let [world-unit-scale (float (/ tile-size))
        world-width (* world-viewport-width world-unit-scale)
        world-height (* world-viewport-height world-unit-scale)
        batch (atom nil)
        skin (atom nil)
        ui-stage (atom nil)
        world-viewport (atom nil)
        camera (atom nil)
        db (atom nil)
        textures (atom nil)
        tiled-map (atom nil)
        buttons (for [[label level-fn] level-fns]
                  [(str "Generate " label)
                   (fn []
                     (Disposable/.dispose @tiled-map)
                     (reset! tiled-map
                             (generate-level @db @textures @camera level-fn)))])]
    (reify ApplicationListener
      (create [_]
        (reset! batch (SpriteBatch.))
        (reset! skin (create-skin (.internal ^Files Gdx/files ui-skin-path))) ; same ?
        (reset! ui-stage (create-stage @batch
                                       (FitViewport. ui-viewport-width ui-viewport-height) ; requires gl context ?
                                       (let [^Skin skin @skin
                                             window (Window. "Edit" skin)]
                                         (doseq [[label on-click!] buttons]
                                           (.add window (text-button skin label on-click!))
                                           (.row window))
                                         (.pack window)
                                         window)))
        (.setInputProcessor ^Input Gdx/input ^InputProcessor @ui-stage)
        (reset! world-viewport (create-viewport world-width world-height)) ; same requires context?
        (reset! camera (.getCamera ^Viewport @world-viewport)) ; ?? sep?
        (reset! db (db/create)) ; needs reloading?
        (reset! textures (textures/create Gdx/files textures-config))
        (reset! tiled-map (generate-level @db @textures @camera initial-level-fn)))

      (dispose [_]
        (Disposable/.dispose @batch)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose @tiled-map))

      (render [_]
        (let [camera* @camera]
          (ScreenUtils/clear 0 0 0 0)
          (moon-tiled-map/draw! @tiled-map
                                @batch
                                world-unit-scale
                                (.getCamera ^Viewport @world-viewport)
                                (constantly (color/float-bits [1 1 1 1])))
          (handle-controls! camera*)
          (.act ^Stage @ui-stage)
          (.draw ^Stage @ui-stage)))

      (resize [_ width height]
        (.update (.getViewport ^Stage @ui-stage) width height true)
        (.update ^Viewport @world-viewport width height false))

      (pause [_])

      (resume [_]))))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener config)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
