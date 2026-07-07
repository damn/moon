(ns clojure.levelgen-test-start
  (:require [clojure.add-listener]
            [clojure.all-raw :refer [all-raw]]
            [clojure.app-event :as app-event]
            [clojure.create-textures :as create-textures]
            [clojure.creature-tiles :as creature-tiles]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.float-bits]
            [clojure.gdx-draw-tiled-map :as draw-tiled-map]
            [clojure.gdx.files :as gdx-files]
            [clojure.gdx.graphics :as gdx-graphics]
            [clojure.gdx.input :as gdx-input]
            [clojure.get :as get]
            [clojure.get-property :as get-property]
            [clojure.gl20 :as gl20]
            [clojure.graphics :as graphics]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.input :as input]
            [clojure.key-pressed :as key-pressed?]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.modules :as modules]
            [clojure.moon-db :as db]
            [clojure.moon-textures :as textures]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :refer [set-position!]]
            [clojure.scene2d-stage]
            [clojure.set-ctx :as set-ctx]
            [clojure.skin :as skin]
            [clojure.sprite-batch :as sprite-batch]
            [clojure.stage :as stage]
            [clojure.state :refer [application]]
            [clojure.tiled-map :as tiled-map]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tmx :as tmx]
            [clojure.uf-caves :as uf-caves]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.utils-change-listener :as utils-change-listener]
            [clojure.viewport :as viewport]
            [clojure.zoom-to-rect :as zoom-to-rect]))

(defn- button-change-listener [f]
  (utils-change-listener/create (app-event/f f)))

(defn- generate-level
  [{:keys [ctx/camera
           ctx/db
           ctx/textures]
    :as ctx}
   level-fn]
  (let [level (level-fn
               {:level/creature-properties (creature-tiles/prepare
                                            (all-raw db :properties/creatures)
                                            #(textures/texture-region textures %))
                :textures textures})
        tiled-map (:tiled-map level)
        ctx (assoc ctx :ctx/tiled-map tiled-map)]
    (assert tiled-map)
    (-> tiled-map
        tiled-map/get-layers
        (get/f "creatures")
        (tiled-map-tile-layer/set-visible! true))
    (set-position! camera [(/ (get-property/f tiled-map "width") 2)
                           (/ (get-property/f tiled-map "height") 2)])
    (zoom-to-rect/f camera {:left [0 0]
                            :top [0 (get-property/f tiled-map "height")]
                            :right [(get-property/f tiled-map "width") 0]
                            :bottom [0 0]})
    ctx))

(defn- create-ctx
  [config]
  (let [files (gdx-files/f)
        input (gdx-input/f)
        graphics (gdx-graphics/f)
        sprite-batch (sprite-batch/new)
        ui-viewport (fit-viewport/create (:ui-viewport-width config)
                                         (:ui-viewport-height config))
        world-unit-scale (float (/ (:tile-size config)))
        stage (clojure.scene2d-stage/create ui-viewport sprite-batch)
        skin (skin/new (files/internal files (:ui-skin-path config)))
        world-viewport (let [world-width (* (:world-viewport-width config) world-unit-scale)
                             world-height (* (:world-viewport-height config) world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (orthographic-camera/new)
                                                (orthographic-camera/set-to-ortho! false world-width world-height))))
        ctx {:ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/zoom-speed (:zoom-speed config)
             :ctx/camera-movement-speed (:camera-movement-speed config)
             :ctx/world-unit-scale world-unit-scale
             :ctx/db (db/create)
             :ctx/textures (create-textures/f files (:textures-config config))
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (:viewport/camera world-viewport)}
        ctx (generate-level ctx (:initial-level-fn config))]
    (input/set-input-processor! input stage)
    (stage/add-actor! (:ctx/stage ctx)
                      (window/create
                       {:title "Edit"
                        :skin skin
                        :table/rows (for [[label level-fn] (:level-fns config)
                                          :let [on-click #(do
                                                            (disposable/dispose! (:ctx/tiled-map %))
                                                            (generate-level % level-fn))]]
                                      [{:actor (doto (text-button/create
                                                      {:text (str "Generate " label)
                                                       :skin skin})
                                               (clojure.add-listener/f (button-change-listener on-click)))}])}))
    ctx))

(defn- dispose-ctx
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (disposable/dispose! skin)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))

(defn- render-ctx
  [{:keys [ctx/input
           ctx/camera
           ctx/zoom-speed
           ctx/camera-movement-speed
           ctx/sprite-batch
           ctx/tiled-map
           ctx/world-viewport
           ctx/world-unit-scale
           ctx/graphics
           ctx/stage] :as ctx}]
  (let [ctx (or (:stage/ctx stage)
                ctx)]
    (set-ctx/f stage ctx))
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (constantly (clojure.float-bits/f [1 1 1 1])))
  (when (key-pressed?/f input :input.keys/minus) (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed)))
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position/f camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left) (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up) (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down) (apply-position 1 -)))
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(defn- resize-ctx
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(defn -main []
  (let [config {:initial-level-fn uf-caves/create
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
                :camera-movement-speed 1}]
    (lwjgl3-application/f!
     {:title "Levelgen Test"
      :windowed-mode {:width 1440 :height 900}
      :foreground-fps 60}
     {:create! (fn [] (reset! application (create-ctx config)))
      :dispose! (fn [] (dispose-ctx @application))
      :render! (fn [] (swap! application render-ctx))
      :resize! (fn [width height] (resize-ctx @application width height))
      :pause! (fn [])
      :resume! (fn [])})))
