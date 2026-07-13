(ns clojure.gdx.maps.tiled.tiled-map
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.maps.map-layers :as map-layers]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.gdx.math.vector3 :as vector3]
            [clojure.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as moon-tile-layer]))

(defn get-property [tiled-map k]
  (-> tiled-map
      tiled-map/getProperties
      (map-properties/get k)))

(defn create-layer
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (tiled-map/getProperties tiled-map)]
    (moon-tile-layer/create
     {:width      (map-properties/get props "width")
      :height     (map-properties/get props "height")
      :tilewidth  (map-properties/get props "tilewidth")
      :tileheight (map-properties/get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))

(defn add-layer! [tiled-map layer]
  (map-layers/add (tiled-map/getLayers tiled-map)
                  (create-layer tiled-map layer)))

(defn create
  [{:keys [properties layers]}]
  (let [tiled-map (tiled-map/new)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/getProperties tiled-map) k v))
    (doseq [layer layers]
      (add-layer! tiled-map layer))
    tiled-map))

(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (map-layers/get (tiled-map/getLayers tiled-map) layer-name)]
    (for [x (range (tiled-map-tile-layer/getWidth layer))
          y (range (tiled-map-tile-layer/getHeight layer))
          :let [position [x y]
                cell (tiled-map-tile-layer/getCell layer x y)]
          :when cell
          :let [value (map-properties/get (tiled-map-tile/getProperties (tiled-map-tile-layer-cell/getTile cell))
                                           property-key)]
          :when value]
      [position value])))

(defn tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (tiled-map-tile-layer/getCell layer x y)]
      (let [value (map-properties/get (tiled-map-tile/getProperties (tiled-map-tile-layer-cell/getTile cell))
                                      "movement")]
        (assert value
                (str "Value for :movement at position "
                     position " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (map-properties/get (tiled-map/getProperties tiled-map) "height"))
                                                                    (position 1))]
                     " and layer " (tiled-map-tile-layer/getName layer) " is undefined."))
        value))))

(defn movement-property-layers [tiled-map]
  (->> tiled-map
       tiled-map/getLayers
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/getProperties %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(tiled-map-tile-layer/getName layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

(defn prepare-creature-tiles [creature-properties image->texture-region]
  (for [{:keys [entity/animation
                creature/level
                property/id]} creature-properties
        :let [image (first (:animation/frames animation))
              texture-region (image->texture-region image)]]
    {:creature/level level
     :tile/id id
     :tile/texture-region texture-region}))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (add-layer! tiled-map
              (let [creature-tile (memoize
                                   (fn [{:keys [tile/id
                                                tile/texture-region]}]
                                     (assert (and id
                                                  texture-region))
                                     (let [tile (static-tiled-map-tile/new texture-region)]
                                       (map-properties/put! (static-tiled-map-tile/getProperties tile) "id" id)
                                       tile)))]
                {:name "creatures"
                 :visible? false
                 :tiles (for [[position creature-property] spawn-positions]
                          [position (creature-tile creature-property)])})))

(defn- draw-tile!
  [x
   y
   tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [region (tiled-map-tile/getTextureRegion tile)
        x1 (+ x (* (tiled-map-tile/getOffsetX tile) unit-scale))
        y1 (+ y (* (tiled-map-tile/getOffsetY tile) unit-scale))
        x2 (+ x1 (* (texture-region/get-region-width region) unit-scale))
        y2 (+ y1 (* (texture-region/get-region-height region) unit-scale))
        u1 (texture-region/get-u region)
        v1 (texture-region/get-v2 region)
        u2 (texture-region/get-u2 region)
        v2 (texture-region/get-v region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (aset-float verts batch/X1 x1)
    (aset-float verts batch/Y1 y1)
    (aset-float verts batch/C1 color11)
    (aset-float verts batch/U1 u1)
    (aset-float verts batch/V1 v1)
    (aset-float verts batch/X2 x1)
    (aset-float verts batch/Y2 y2)
    (aset-float verts batch/C2 color12)
    (aset-float verts batch/U2 u1)
    (aset-float verts batch/V2 v2)
    (aset-float verts batch/X3 x2)
    (aset-float verts batch/Y3 y2)
    (aset-float verts batch/C3 color22)
    (aset-float verts batch/U3 u2)
    (aset-float verts batch/V3 v2)
    (aset-float verts batch/X4 x2)
    (aset-float verts batch/Y4 y1)
    (aset-float verts batch/C4 color21)
    (aset-float verts batch/U4 u2)
    (aset-float verts batch/V4 v1)
    (batch/draw batch
                (texture-region/get-texture region)
                verts
                0
                num-vertices)))

(defn- draw-tile-layer!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (batch/getColor batch)
        layer-width (tiled-map-tile-layer/getWidth layer)
        layer-height (tiled-map-tile-layer/getHeight layer)
        layer-tile-width (* (tiled-map-tile-layer/getTileWidth layer) unit-scale)
        layer-tile-height (* (tiled-map-tile-layer/getTileHeight layer) unit-scale)
        layer-offset-x (* (tiled-map-tile-layer/getRenderOffsetX layer) unit-scale)
        layer-offset-y (* (- (tiled-map-tile-layer/getRenderOffsetY layer)) unit-scale)
        col1 (max 0
                  (int (/ (- (:x view-bounds) layer-offset-x)
                          layer-tile-width)))
        col2 (min layer-width
                  (int (/ (+ (:x view-bounds)
                             (:width view-bounds)
                             layer-tile-width
                             (- layer-offset-x))
                          layer-tile-width)))
        row1 (max 0
                  (int (/ (- (:y view-bounds) layer-offset-y)
                          layer-tile-height)))
        row2 (min layer-height
                  (int (/ (+ (:y view-bounds)
                             (:height view-bounds)
                             layer-tile-height
                             (- layer-offset-y))
                          layer-tile-height)))
        x-start (+ (* col1 layer-tile-width)
                   layer-offset-x)
        verts (aclone vertices)]
    (loop [row row2
           y (+ (* row2 layer-tile-height)
                layer-offset-y)]
      (when (>= row row1)
        (loop [col col1
               x x-start]
          (when (< col col2)
            (when-let [cell (tiled-map-tile-layer/getCell layer col row)]
              (when-let [tile (tiled-map-tile-layer-cell/getTile cell)]
                (draw-tile! x
                            y
                            tile
                            unit-scale
                            color-setter
                            batch-color
                            verts
                            batch
                            num-vertices)))
            (recur (inc col)
                   (+ x layer-tile-width))))
        (recur (dec row)
               (- y layer-tile-height))))))

(defn draw!
  [tiled-map
   batch
   world-unit-scale
   camera
   color-setter]
  (batch/setProjectionMatrix batch (orthographic-camera/combined camera))
  (batch/begin batch)
  (let [width  (* (orthographic-camera/viewport-width camera) (orthographic-camera/zoom camera))
        height (* (orthographic-camera/viewport-height camera) (orthographic-camera/zoom camera))
        up (orthographic-camera/up camera)
        w (+ (* width  (Math/abs (float (vector3/y up))))
             (* height (Math/abs (float (vector3/x up)))))
        h (+ (* height (Math/abs (float (vector3/y up))))
             (* width  (Math/abs (float (vector3/x up)))))
        pos (orthographic-camera/position-vec3 camera)
        view-bounds {:x (- (vector3/x pos) (/ w 2))
                     :y (- (vector3/y pos) (/ h 2))
                     :width w
                     :height h}]
    (doseq [layer (filter tiled-map-tile-layer/isVisible (tiled-map/getLayers tiled-map))]
      (draw-tile-layer! layer
                        batch
                        world-unit-scale
                        view-bounds
                        color-setter)))
  (batch/end batch))
