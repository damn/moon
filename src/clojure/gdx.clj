(ns clojure.gdx
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx Audio
                             Files
                             Graphics)
           (com.badlogic.gdx.graphics Color
                                      GL20
                                      OrthographicCamera
                                      Texture)
           (com.badlogic.gdx.graphics.g2d Batch
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group
                                            Stage)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn clear! [graphics r g b a]
  (let [gl (Graphics/.getGL20 graphics)]
    (GL20/.glClearColor gl r g b a)
    (GL20/.glClear gl GL20/GL_COLOR_BUFFER_BIT)))

(defn draw-tiled-map-tile!
  [x
   y
   ^TiledMapTile tile
   unit-scale
   color-setter
   batch-color
   verts
   batch
   num-vertices]
  (let [^TextureRegion region (.getTextureRegion tile)
        x1 (+ x (* (.getOffsetX tile) unit-scale))
        y1 (+ y (* (.getOffsetY tile) unit-scale))
        x2 (+ x1 (* (.getRegionWidth region) unit-scale))
        y2 (+ y1 (* (.getRegionHeight region) unit-scale))
        u1 (.getU region)
        v1 (.getV2 region)
        u2 (.getU2 region)
        v2 (.getV region)
        color11 (float (color-setter batch-color x1 y1))
        color12 (float (color-setter batch-color x1 y2))
        color22 (float (color-setter batch-color x2 y2))
        color21 (float (color-setter batch-color x2 y1))]
    (aset-float verts Batch/X1 x1)
    (aset-float verts Batch/Y1 y1)
    (aset-float verts Batch/C1 color11)
    (aset-float verts Batch/U1 u1)
    (aset-float verts Batch/V1 v1)
    (aset-float verts Batch/X2 x1)
    (aset-float verts Batch/Y2 y2)
    (aset-float verts Batch/C2 color12)
    (aset-float verts Batch/U2 u1)
    (aset-float verts Batch/V2 v2)
    (aset-float verts Batch/X3 x2)
    (aset-float verts Batch/Y3 y2)
    (aset-float verts Batch/C3 color22)
    (aset-float verts Batch/U3 u2)
    (aset-float verts Batch/V3 v2)
    (aset-float verts Batch/X4 x2)
    (aset-float verts Batch/Y4 y1)
    (aset-float verts Batch/C4 color21)
    (aset-float verts Batch/U4 u2)
    (aset-float verts Batch/V4 v1)
    (Batch/.draw batch
                 ^Texture (.getTexture region)
                 ^floats verts
                 (int 0)
                 (int num-vertices))))

(defn draw-tiled-map-tile-layer!
  [^TiledMapTileLayer layer
   ^Batch batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (.getColor batch)
        layer-width (.getWidth layer)
        layer-height (.getHeight layer)
        layer-tile-width (* (.getTileWidth layer) unit-scale)
        layer-tile-height (* (.getTileHeight layer) unit-scale)
        layer-offset-x (* (.getRenderOffsetX layer) unit-scale)
        ; offset in tiled is y down, so we flip it
        layer-offset-y (* (- (.getRenderOffsetY layer)) unit-scale)
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
            (when-let [^TiledMapTileLayer$Cell cell (.getCell layer col row)]
              (when-let [tile (.getTile cell)]
                (draw-tiled-map-tile! x
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

(defn draw-tiled-map!
  [^Batch batch
   world-unit-scale
   ^OrthographicCamera camera
   ^TiledMap tiled-map
   color-setter]
  (.setProjectionMatrix batch (.combined camera))
  (.begin batch)
  (let [width  (* (.viewportWidth  camera) (.zoom camera))
        height (* (.viewportHeight camera) (.zoom camera))
        w (+ (* width  (Math/abs (.y (.up camera))))
             (* height (Math/abs (.x (.up camera)))))
        h (+ (* height (Math/abs (.y (.up camera))))
             (* width  (Math/abs (.x (.up camera)))))
        viewBounds {:x (- (.x (.position camera)) (/ w 2))
                    :y (- (.y (.position camera)) (/ h 2))
                    :width w
                    :height h}]
    (doseq [^TiledMapTileLayer layer (filter TiledMapTileLayer/.isVisible (.getLayers tiled-map))]
      (draw-tiled-map-tile-layer! layer
                                  batch
                                  (float world-unit-scale)
                                  viewBounds
                                  color-setter)))
  (.end batch))

(defn float-bits [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))

(defprotocol AddActor
  (add-actor! [_ actor]))

(extend-type Group
  AddActor
  (add-actor! [group actor]
    (.addActor group actor)))

(extend-type Stage
  AddActor
  (add-actor! [stage actor]
    (.addActor stage actor)))

(defn new-sound [audio file-handle]
  (Audio/.newSound audio file-handle))

(defn internal [files path]
  (Files/.internal files path))

(defn sprite-batch []
  (SpriteBatch.))

(defn fit-viewport
  ([width height]
   (proxy [FitViewport ILookup] [width height]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this)))))
  ([width height camera]
   (proxy [FitViewport ILookup] [width height camera]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this))))))

(defn add-listener! [actor listener]
  (Actor/.addListener actor listener))

(defn get-stage [actor]
  (Actor/.getStage actor))

(defn dispose! [disposable]
  (Disposable/.dispose disposable))

(defn get-children [group]
  (Group/.getChildren group))

(defn find-actor [group name]
  (Group/.findActor group name))

(defn clear-children! [group]
  (Group/.clearChildren group))

(defn group []
  (Group.))
