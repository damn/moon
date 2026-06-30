(ns clojure.gdx
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx ApplicationListener
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input
                             Input$Buttons
                             Input$Keys)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      GL20
                                      OrthographicCamera
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d Batch
                                          BitmapFont
                                          SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event
                                            Group
                                            Stage
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup
                                               Cell
                                               CheckBox
                                               HorizontalGroup
                                               Image
                                               ImageButton
                                               Label
                                               ScrollPane
                                               SelectBox
                                               Skin
                                               Stack
                                               Table
                                               TextButton
                                               TextField
                                               TextTooltip
                                               TooltipManager
                                               Widget
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  ClickListener
                                                  Drawable
                                                  Layout
                                                  TextureRegionDrawable)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.maps MapLayers
                                  MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell
                                        TmxMapLoader)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)
           (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle
                                  Vector2
                                  Vector3)
           (com.badlogic.gdx.utils Align
                                   Disposable
                                   Os
                                   SharedLibraryLoader)
           (com.badlogic.gdx.utils.viewport FitViewport
                                            Viewport)
           (org.lwjgl.system Configuration)))

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

(defn tiled-map-tile-layer
  [{:keys [width
           height
           tilewidth
           tileheight
           name
           visible?
           map-properties
           tiles]}]
  {:pre [(string? name)
         (boolean? visible?)]}
  (let [layer (doto (TiledMapTileLayer. width height tilewidth tileheight)
                (.setName name)
                (.setVisible visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (MapProperties/.put (.getProperties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (.setCell ^TiledMapTileLayer layer
                x
                y
                (doto (TiledMapTileLayer$Cell.)
                  (.setTile tile))))
    layer))

(defn create-layer
  [^TiledMap tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (.getProperties tiled-map)]
    (tiled-map-tile-layer
     {:width      (.get props "width")
      :height     (.get props "height")
      :tilewidth  (.get props "tilewidth")
      :tileheight (.get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))

(defn add-layer! [^TiledMap tiled-map layer]
  (.add (.getLayers tiled-map)
        (create-layer tiled-map layer)))

(defn set-opts! [^Cell cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (.fillX cell)
      :fill-y?    (.fillY cell)
      :expand?    (.expand cell)
      :expand-x?  (.expandX cell)
      :expand-y?  (.expandY cell)
      :bottom?    (.bottom cell)
      :colspan    (.colspan cell (int arg))
      :pad        (.pad cell (float arg))
      :pad-top    (.padTop cell (float arg))
      :pad-bottom (.padBottom cell (float arg))
      :width      (.width cell (float arg))
      :height     (.height cell (float arg))
      :center?    (.center cell)
      :right?     (.right cell)
      :left?      (.left cell))))

(defn audio []
  Gdx/audio)

(defn files []
  Gdx/files)

(defn graphics []
  Gdx/graphics)

(defn input []
  Gdx/input)

(defn vector2 ^Vector2 [[x y]]
  (Vector2. x y))

(defn vector2-clojurize [^Vector2 v2]
  [(.x v2) (.y v2)])

(defn vector3-clojurize [^Vector3 v3]
  [(.x v3) (.y v3) (.z v3)])

(defn color [[r g b a]] (Color. r g b a))

(defn circle [x y radius] (Circle. x y radius))

(defn rectangle [x y width height] (Rectangle. x y width height))

(defn rectangle-contains? [^Rectangle rectangle [x y]]
  (.contains rectangle x y))

(defn rectangle-overlaps? [^Rectangle a ^Rectangle b]
  (.overlaps a b))

(defn intersector-overlaps? [^Circle circle ^Rectangle rectangle]
  (Intersector/overlaps circle rectangle))

;; --- Input ---

(defn k-to-input-key [k]
  (case k
    :input.keys/d Input$Keys/D
    :input.keys/a Input$Keys/A
    :input.keys/w Input$Keys/W
    :input.keys/s Input$Keys/S
    :input.keys/minus Input$Keys/MINUS
    :input.keys/equals Input$Keys/EQUALS
    :input.keys/p Input$Keys/P
    :input.keys/space Input$Keys/SPACE
    :input.keys/escape Input$Keys/ESCAPE
    :input.keys/i Input$Keys/I
    :input.keys/e Input$Keys/E
    :input.keys/enter Input$Keys/ENTER
    :input.keys/left Input$Keys/LEFT
    :input.keys/right Input$Keys/RIGHT
    :input.keys/up Input$Keys/UP
    :input.keys/down Input$Keys/DOWN))

(defn k-to-input-button [k]
  (case k
    :input.buttons/left Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT))

(defn input-get-x [^Input input] (Input/.getX input))
(defn input-get-y [^Input input] (Input/.getY input))

(defn input-is-key-pressed [^Input input k]
  (Input/.isKeyPressed input (k-to-input-key k)))

(defn input-is-key-just-pressed [^Input input k]
  (Input/.isKeyJustPressed input (k-to-input-key k)))

(defn input-is-button-just-pressed [^Input input k]
  (Input/.isButtonJustPressed input (k-to-input-button k)))

(defn input-set-input-processor! [^Input input processor]
  (Input/.setInputProcessor input processor))

;; --- Graphics ---

(defn graphics-set-cursor! [^Graphics graphics cursor]
  (Graphics/.setCursor graphics cursor))

(defn graphics-new-cursor [^Graphics graphics ^Pixmap pixmap hotspot-x hotspot-y]
  (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y))

(defn graphics-get-delta-time [^Graphics graphics]
  (Graphics/.getDeltaTime graphics))

(defn graphics-get-frames-per-second [^Graphics graphics]
  (Graphics/.getFramesPerSecond graphics))

;; --- Viewport ---

(defn unproject [^Viewport viewport xy]
  (-> viewport
      (.unproject (vector2 xy))
      vector2-clojurize))

(defn viewport-update [^Viewport viewport width height center?]
  (.update viewport width height center?))

;; --- Files ---

(defn recursively-search [^FileHandle file-handle extensions]
  (loop [[file & remaining] (.list file-handle)
         result []]
    (cond (nil? file)
          result

          (.isDirectory ^FileHandle file)
          (recur (concat remaining (.list ^FileHandle file)) result)

          (extensions (.extension ^FileHandle file))
          (recur remaining (conj result (.path ^FileHandle file)))

          :else
          (recur remaining result))))

;; --- Texture / Batch / Font ---

(defn texture
  [^FileHandle file-handle]
  (Texture. file-handle))

(defn pixmap->texture
  [^Pixmap pixmap]
  (Texture. pixmap))

(defn texture-region
  ([^Texture texture]
   (TextureRegion. texture))
  ([^Texture texture x y w h]
   (TextureRegion. texture (int x) (int y) (int w) (int h))))

(defn texture-region-get-region-width [^TextureRegion region]
  (.getRegionWidth region))

(defn texture-region-get-region-height [^TextureRegion region]
  (.getRegionHeight region))

(defn texture-region-drawable [drawable]
  (TextureRegionDrawable. ^TextureRegion drawable))

(defn texture-region-drawable-set-min-size! [drawable w h]
  (.setMinSize ^TextureRegionDrawable drawable w h))

(defn texture-region-drawable-tint! [drawable color]
  (.tint ^TextureRegionDrawable drawable color))

(defn batch-set-color! [^Batch batch r g b a]
  (Batch/.setColor batch r g b a))

(defn batch-set-projection-matrix! [^Batch batch matrix]
  (Batch/.setProjectionMatrix batch matrix))

(defn batch-begin! [^Batch batch] (Batch/.begin batch))
(defn batch-end! [^Batch batch] (Batch/.end batch))

(defn batch-draw-texture-region!
  [^Batch batch ^TextureRegion texture-region x y & {:keys [center? rotation w h]}]
  (let [w (float (or w (.getRegionWidth texture-region)))
        h (float (or h (.getRegionHeight texture-region)))]
    (if center?
      (.draw batch
             texture-region
             (- (float x) (/ w 2))
             (- (float y) (/ h 2))
             (/ w 2)
             (/ h 2)
             w
             h
             1
             1
             (or rotation 0))
      (.draw batch texture-region (float x) (float y) w h))))

(defn font-get-data [^BitmapFont font] (.getData font))
(defn font-get-line-height [^BitmapFont font] (.getLineHeight font))
(defn font-get-scale-x [^BitmapFont font] (.scaleX (.getData font)))

(defn font-set-scale! [^BitmapFont font scale]
  (.setScale (.getData font) scale))

(defn font-set-markup-enabled! [^BitmapFont font enabled?]
  (set! (.markupEnabled (.getData font)) enabled?))

(defn font-set-use-integer-positions! [^BitmapFont font use?]
  (.setUseIntegerPositions font use?))

(defn font-draw! [^BitmapFont font batch text x y target-width align wrap?]
  (.draw font batch text (float x) (float y) (float target-width) align wrap?))

;; --- Camera ---

(defn orthographic-camera [] (OrthographicCamera.))

(defn camera-set-to-ortho! [^OrthographicCamera camera y-down? world-width world-height]
  (.setToOrtho camera y-down? world-width world-height))

(defn camera-set-zoom! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))

(defn camera-update! [^OrthographicCamera camera] (.update camera))

(defn camera-zoom [^OrthographicCamera camera] (.zoom camera))

(defn camera-position [^OrthographicCamera camera] (.position camera))

(defn camera-set-position! [^OrthographicCamera camera x y]
  (set! (.x (.position camera)) x)
  (set! (.y (.position camera)) y))

(defn camera-viewport-width [^OrthographicCamera camera] (.viewportWidth camera))
(defn camera-viewport-height [^OrthographicCamera camera] (.viewportHeight camera))
(defn camera-combined [^OrthographicCamera camera] (.combined camera))
(defn camera-up [^OrthographicCamera camera] (.up camera))
(defn camera-frustum-plane-points [^OrthographicCamera camera]
  (.planePoints (.frustum camera)))

;; --- Sound ---

(defn sound-play [^Sound sound] (Sound/.play sound))

;; --- Maps ---

(defn tiled-map [] (TiledMap.))

(defn tmx-map-loader-load [path]
  (.load (TmxMapLoader.) path))

(defn tiled-map-get-properties [^TiledMap tiled-map] (.getProperties tiled-map))
(defn tiled-map-get-layers [^TiledMap tiled-map] (.getLayers tiled-map))

(defn map-layers-get [^MapLayers layers name] (.get layers ^String name))

(defn map-properties-get [^MapProperties props key] (MapProperties/.get props key))
(defn map-properties-put! [^MapProperties props key value] (MapProperties/.put props key value))

(defn map-properties-clojurize [^MapProperties props]
  (zipmap (.getKeys props) (.getValues props)))

(defn tiled-map-tile-layer-get-cell [^TiledMapTileLayer layer x y] (.getCell layer x y))
(defn tiled-map-tile-layer-get-width [^TiledMapTileLayer layer] (.getWidth layer))
(defn tiled-map-tile-layer-get-height [^TiledMapTileLayer layer] (.getHeight layer))
(defn tiled-map-tile-layer-get-name [^TiledMapTileLayer layer] (.getName layer))
(defn tiled-map-tile-layer-is-visible? [^TiledMapTileLayer layer] (.isVisible layer))
(defn tiled-map-tile-layer-get-properties [^TiledMapTileLayer layer] (.getProperties layer))
(defn tiled-map-tile-layer-set-visible! [^TiledMapTileLayer layer visible?] (.setVisible layer visible?))

(defn cell-get-tile [^TiledMapTileLayer$Cell cell] (.getTile cell))
(defn tile-get-properties [^TiledMapTile tile]
  (.getProperties tile))

(defn static-tiled-map-tile [^TextureRegion texture-region]
  (StaticTiledMapTile. texture-region))

(defn static-tiled-map-tile-copy [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))

;; --- Actor ---

(defn set-name! [^Actor actor name] (Actor/.setName actor name))
(defn set-visible! [^Actor actor visible?] (Actor/.setVisible actor visible?))
(defn visible? [^Actor actor] (Actor/.isVisible actor))
(defn remove! [^Actor actor] (Actor/.remove actor))
(defn set-user-object! [^Actor actor obj] (Actor/.setUserObject actor obj))
(defn get-user-object [^Actor actor] (Actor/.getUserObject actor))
(defn get-x [^Actor actor] (Actor/.getX actor))
(defn get-y [^Actor actor] (Actor/.getY actor))
(defn get-width [^Actor actor] (Actor/.getWidth actor))
(defn get-height [^Actor actor] (Actor/.getHeight actor))
(defn get-parent [^Actor actor] (Actor/.getParent actor))
(defn get-name [^Actor actor] (Actor/.getName actor))

(defn set-touchable! [^Actor actor touchable] (Actor/.setTouchable actor touchable))

(defn stage-to-local-coordinates [^Actor actor coords]
  (Actor/.stageToLocalCoordinates actor coords))

(defn actor-hit [^Actor actor x y touchable?]
  (Actor/.hit actor x y touchable?))

(defn set-position!
  ([^Actor actor x y] (Actor/.setPosition actor x y))
  ([^Actor actor x y align] (Actor/.setPosition actor x y align)))

(defn actor [{:keys [act! draw!]}]
  (proxy [Actor] []
    (act [delta]
      (when act! (act! this delta))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (when draw! (draw! this batch parent-alpha)))))

(defn label? [obj] (instance? Label obj))
(defn window? [obj] (instance? Window obj))
(defn button? [obj] (instance? Button obj))

(defn button-class? [actor]
  (some #(= Button %) (supers (class actor))))

;; --- Stage ---

(defn stage-act! [^Stage stage] (Stage/.act stage))
(defn stage-draw! [^Stage stage] (Stage/.draw stage))
(defn stage-clear! [^Stage stage] (Stage/.clear stage))

;; --- Event ---

(defn event-get-stage [^Event event] (Event/.getStage event))

;; --- Layout ---

(defn pack! [^Layout layout] (Layout/.pack layout))

;; --- Table ---

(defn table [] (Table.))
(defn table-add! [^Table table actor] (Table/.add table ^Actor actor))
(defn table-row! [^Table table] (Table/.row table))
(defn table-defaults [^Table table] (Table/.defaults table))
(defn table-set-fill-parent! [^Table table fill-parent?] (.setFillParent table fill-parent?))

;; --- Window ---

(defn window [^String title ^Skin skin] (Window. title skin))
(defn window-get-title-table [^Window window] (Window/.getTitleTable window))
(defn window-get-title-label [^Window window] (Window/.getTitleLabel window))
(defn window-set-modal! [^Window window modal?] (Window/.setModal window modal?))
(defn window-set-fill-parent! [^Window window fill-parent?] (Window/.setFillParent window fill-parent?))

;; --- Label ---

(defn label [^String text ^Skin skin] (Label. text skin))
(defn label-set-text! [^Label label ^String text] (Label/.setText label text))

;; --- Image ---

(defn image [drawable-or-texture]
  (if (instance? Texture drawable-or-texture)
    (Image. ^Texture drawable-or-texture)
    (Image. ^Drawable drawable-or-texture)))

(defn image-set-drawable! [^Image image drawable] (Image/.setDrawable image drawable))
(defn image-set-min-size! [^Image image w h] (Image/.setMinSize image w h))
(defn image-tint! [^Image image color] (Image/.tint image color))
(defn image-set-name! [^Image image name] (Image/.setName image name))
(defn image-set-user-object! [^Image image obj] (Image/.setUserObject image obj))

;; --- ButtonGroup ---

(defn button-group [] (ButtonGroup.))
(defn button-group-add! [^ButtonGroup group button] (ButtonGroup/.add group button))
(defn button-group-remove! [^ButtonGroup group button] (ButtonGroup/.remove group button))
(defn button-group-get-checked [^ButtonGroup group] (ButtonGroup/.getChecked group))
(defn button-group-set-max-check-count! [^ButtonGroup group n] (ButtonGroup/.setMaxCheckCount group n))
(defn button-group-set-min-check-count! [^ButtonGroup group n] (ButtonGroup/.setMinCheckCount group n))

;; --- HorizontalGroup ---

(defn horizontal-group [] (HorizontalGroup.))
(defn horizontal-group-space! [^HorizontalGroup group n] (.space group n))
(defn horizontal-group-pad! [^HorizontalGroup group n] (.pad group n))

;; --- TextField ---

(defn text-field [^String text ^Skin skin] (TextField. text skin))
(defn text-field-get-text [^TextField field] (TextField/.getText field))
(defn text-field-set-text! [^TextField field ^String text] (TextField/.setText field text))

;; --- Other UI ---

(defn text-button [^String text ^Skin skin] (TextButton. text skin))
(defn text-tooltip [^String tooltip ^Skin skin] (TextTooltip. tooltip skin))
(defn scroll-pane [^Actor actor ^Skin skin] (ScrollPane. actor skin))
(defn stack [] (Stack.))
(defn select-box [^Skin skin] (SelectBox. skin))

(defn select-box-set-items! [^SelectBox box items]
  (.setItems box ^"[Ljava.lang.Object;" (into-array items)))

(defn select-box-set-selected! [^SelectBox box selected] (.setSelected box selected))
(defn select-box-get-selected [^SelectBox box] (SelectBox/.getSelected box))

(defn check-box [^String text ^Skin skin] (CheckBox. text skin))
(defn check-box-set-checked! [^CheckBox box checked?] (.setChecked box checked?))
(defn check-box-is-checked? [^CheckBox box] (CheckBox/.isChecked box))

(defn image-button [drawable] (ImageButton. ^TextureRegionDrawable drawable))
(defn skin [^FileHandle file-handle] (Skin. file-handle))
(defn skin-get-font [^Skin skin name] (.getFont skin name))

(defn widget [{:keys [draw!]}]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))

(defn click-listener [f]
  (proxy [ClickListener] []
    (clicked [event x y] (f event x y))))

(defn change-listener [f]
  (proxy [ChangeListener] []
    (changed [event actor] (f event actor))))

;; --- Constants ---

(def align-center Align/center)
(def touchable-disabled Touchable/disabled)
(def texture-filter-linear Texture$TextureFilter/Linear)

;; --- Colors / Pixmap / Tooltip ---

(defn colors-put! [name color] (Colors/put name color))

(defn pixmap [^FileHandle file-handle] (Pixmap. file-handle))

(defn pixmap-create [w h format] (Pixmap. w h format))

(defn pixmap-set-color! [^Pixmap pixmap r g b a]
  (Pixmap/.setColor pixmap r g b a))

(defn pixmap-draw-pixel! [^Pixmap pixmap x y]
  (Pixmap/.drawPixel pixmap x y))

(def pixmap-format-rgba8888 Pixmap$Format/RGBA8888)

(defn pixmap-dispose! [^Pixmap pixmap] (Pixmap/.dispose pixmap))

(defn tooltip-manager-set-initial-time! [time]
  (set! (.initialTime (TooltipManager/getInstance)) time))

;; --- FreeType font ---

(defn freetype-font-generator [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn freetype-font-generator-dispose! [generator] (.dispose generator))

(defn freetype-font-parameter []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn freetype-font-generator-generate-font [generator parameter]
  (.generateFont generator parameter))

(defn freetype-font-parameter-set-size! [parameter size]
  (set! (.size parameter) size))

(defn freetype-font-parameter-set-min-filter! [parameter filter]
  (set! (.minFilter parameter) filter))

(defn freetype-font-parameter-set-mag-filter! [parameter filter]
  (set! (.magFilter parameter) filter))

;; --- Application ---

(defn macos-glfw-async! []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async")))

(defn lwjgl3-application! [listener config]
  (Lwjgl3Application. listener config))

(defn lwjgl3-application-configuration []
  (Lwjgl3ApplicationConfiguration.))

(defn lwjgl3-config-set-title! [^Lwjgl3ApplicationConfiguration config title]
  (.setTitle config title))

(defn lwjgl3-config-set-windowed-mode! [^Lwjgl3ApplicationConfiguration config width height]
  (.setWindowedMode config width height))

(defn lwjgl3-config-set-foreground-fps! [^Lwjgl3ApplicationConfiguration config fps]
  (.setForegroundFPS config fps))

(defn application-listener
  [{:keys [create dispose render resize pause resume]}]
  (reify ApplicationListener
    (create [_] (create))
    (dispose [_] (dispose))
    (render [_] (render))
    (resize [_ width height] (resize width height))
    (pause [_] (when pause (pause)))
    (resume [_] (when resume (resume)))))
