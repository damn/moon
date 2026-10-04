(ns moon.game
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [malli.core :as m]
            [malli.error :as me]
            [clojure.math :as math]
            [clojure.string :as str]
            [clojure.repl :as repl]
            [clj-commons.pretty.repl :as pretty-repl]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx Audio Files Application ApplicationListener Gdx Graphics Input Input$Keys InputProcessor Input$Buttons)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.utils Disposable Align)
           (com.badlogic.gdx.math Rectangle Frustum Vector3 Vector2 Circle Intersector)
           (com.badlogic.gdx.graphics OrthographicCamera Color GL20 Colors Cursor Pixmap Pixmap$Format Texture Texture$TextureFilter TextureData)
           (clojure.lang PersistentVector)
           (clojure Stage RayCaster)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData SpriteBatch TextureRegion Batch BitmapFont)
           (com.badlogic.gdx.scenes.scene2d Actor Event Touchable Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image ImageButton Label Skin Stack TextButton TextTooltip Button ButtonGroup HorizontalGroup TooltipManager Widget Cell Table Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener Drawable Layout TextureRegionDrawable ClickListener)
           (com.badlogic.gdx.utils.viewport FitViewport Viewport)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.graphics.glutils PixmapTextureData FileTextureData)
           (space.earlygrey.shapedrawer ShapeDrawer)
           (com.badlogic.gdx.maps MapLayers MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer TiledMapTileLayer$Cell TmxMapLoader TiledMap TiledMapTile))
  (:gen-class))


;; ---- moon.audio ----
(defn audio-create
  [audio files]
  (into {}
        (for [sound-name (-> "config/sounds.edn" io/resource slurp edn/read-string)
              :let [path (format "sounds/%s.wav" sound-name)]]
          [sound-name
           (.newSound ^Audio audio (.internal ^Files files path))])))

(defn play!
  [sounds sound-name]
  (assert (contains? sounds sound-name) (str sound-name))
  (.play ^Sound (get sounds sound-name)))

(defn audio-dispose!
  [sounds]
  (run! Disposable/.dispose (vals sounds)))

;; ---- moon.camera ----
(defn set-zoom! [orthographic-camera amount]
  (set! (.zoom ^OrthographicCamera orthographic-camera) amount)
  (.update ^OrthographicCamera orthographic-camera))

(defn inc-zoom! [orthographic-camera by]
  (set-zoom! orthographic-camera (max 0.1 (+ (.zoom ^OrthographicCamera orthographic-camera) by))))

(defn position [orthographic-camera]
  (let [v3 (.position ^OrthographicCamera orthographic-camera)]
    [(.x ^Vector3 v3) (.y ^Vector3 v3) (.z ^Vector3 v3)]))

(defn set-position! [orthographic-camera [x y]]
  (let [pos (.position ^OrthographicCamera orthographic-camera)]
    (set! (.x ^Vector3 pos) x)
    (set! (.y ^Vector3 pos) y))
  (.update ^OrthographicCamera orthographic-camera))

(defn frustum [orthographic-camera]
  (let [plane-points (mapv (fn [v3]
                             [(.x ^Vector3 v3) (.y ^Vector3 v3) (.z ^Vector3 v3)])
                           (.planePoints ^Frustum (.frustum ^OrthographicCamera orthographic-camera)))
        frustum-points (take 4 plane-points)
        left-x   (apply min (map first  frustum-points))
        right-x  (apply max (map first  frustum-points))
        bottom-y (apply min (map second frustum-points))
        top-y    (apply max (map second frustum-points))]
    [left-x right-x bottom-y top-y]))

(defn visible-tiles [orthographic-camera]
  (let [[left-x right-x bottom-y top-y] (frustum orthographic-camera)]
    (for [x (range (int left-x)   (int right-x))
          y (range (int bottom-y) (+ 2 (int top-y)))]
      [x y])))

;; ---- moon.cell ----
(defrecord R [position
              middle
              adjacent-cells
              movement
              entities
              occupied
              good
              evil])

(defn blocks-vision? [{:keys [movement]}]
  (= movement :none))

(defn cell-blocked? [{:keys [movement]} z-order]
  (case movement
    :none true
    :air (case z-order
           :z-order/flying false
           :z-order/ground true)
    :all false))

(defn pf-blocked? [cell]
  (cell-blocked? cell :z-order/ground))

(defn occupied-by-other? [{:keys [occupied]} eid]
  (some #(not= % eid) occupied))

;; ---- moon.circle ----
(defn outer-rectangle
  [{[x y] :position :keys [radius]}]
  (let [radius (float radius)
        size (* radius 2)]
    {:x (- (float x) radius)
     :y (- (float y) radius)
     :width  size
     :height size}))

;; ---- moon.coll ----
(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [coll]
  (map vector (iterate inc 0) coll))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
  is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll)
        :when (pred elt)]
    idx))

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]]
               (or (let [idx (.indexOf ^PersistentVector k-order k)]
                     (when (not= -1 idx) idx))
                   max-count))
             components)))

(defn sort-by-order [coll get-item-order-k order]
  (sort-by #((get-item-order-k %) order) < coll))

;; ---- moon.color ----
(defn float-bits [[r g b a]]
  (Color/toFloatBits (float r)
                     (float g)
                     (float b)
                     (float a)))

;; ---- moon.faction ----
(defn enemy [faction]
  (case faction
    :evil :good
    :good :evil))

;; ---- moon.level.tmx ----
(defn level-tmx-create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (.load (TmxMapLoader.) tmx-file)
   :start-position start-position})

(defn vampire [_]
  (level-tmx-create {:tmx-file "maps/vampire.tmx"
           :start-position [32 71]}))

;; ---- moon.m ----
(defn dissoc-in [m ks]
  (assert (> (count ks) 1))
  (update-in m (drop-last ks) dissoc (last ks)))

(defn safe-merge [m1 m2]
  {:pre [(not-any? #(contains? m1 %) (keys m2))]}
  (merge m1 m2))

;; ---- moon.malli ----
(defn create-ex-info [schema value]
  (ex-info (str (me/humanize (m/explain schema value)))
           {:value value
            :schema (m/form schema)}))

(defn validate-humanize [schema value]
  (when-not (m/validate schema value)
    (throw (create-ex-info schema value))))

;; ---- moon.map-schema ----
(defn map-form-k->properties
  "Given a map schema gives a map of key to key properties (like :optional)."
  [map-schema]
  (let [[_m _p & ks] map-schema]
    (into {} (for [[k m? _schema] ks]
               [k (if (map? m?) m?)]))))

(defn map-schema-map-keys [map-schema]
  (let [[_m _p & ks] map-schema]
    (for [[k m? _schema] ks]
      k)))

(defn map-schema-optional? [map-schema k]
  (:optional (k (map-form-k->properties map-schema))))

(defn map-schema-optional-keyset [map-schema]
  (set (filter #(map-schema-optional? map-schema %)
               (map-schema-map-keys map-schema))))

;; ---- moon.number ----
(def ^:private float-rounding-error (double 0.000001))

(defn nearly-equal?
  ([x y]
   (nearly-equal? x y float-rounding-error))
  ([a b epsilon]
   (<= (Math/abs (- a b))
       epsilon)))

(defn round-n [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))

(defn readable [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (nearly-equal? x (int x) 0.001))
    (int x)
    (round-n x 2)))

;; ---- moon.ops ----
(defn ops-add [ops other-ops]
  (merge-with + ops other-ops))

(defn ops-remove [ops other-ops]
  (merge-with - ops other-ops))

(defn ops-sort [ops]
  (clojure.core/sort-by (fn [[k]]
                          (case k
                            :op/inc 0
                            :op/mult 1))
                        ops))

(defn ops-apply [ops base-value]
  (reduce (fn [base-value [k value]]
            (case k
              :op/inc  (+ base-value value)
              :op/mult (* base-value (inc (/ value 100)))))
          base-value
          (ops-sort ops)))

;; ---- moon.mods ----
(defn mods-add [mods other-mods]
  (merge-with ops-add mods other-mods))

(defn mods-remove [mods other-mods]
  (merge-with ops-remove mods other-mods))

(defn mods-get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops-apply (modifier-k modifiers)
             base-value))

(defn mods-format-text
  [mods]
  (when (seq mods)
    (str/join "\n"
              (keep (fn [[k modifier-ops]]
                      (str/join "\n"
                                (keep (fn [[op-k v]]
                                        (when-not (zero? v)
                                          (str (case (math/signum v)
                                                 0.0 ""
                                                 1.0 "+"
                                                 -1.0 "")
                                               (case op-k
                                                 :op/inc  (str v)
                                                 :op/mult (str v "%"))
                                               " "
                                               (str/capitalize (name k)))))
                                      (ops-sort modifier-ops))))
                    mods))))

;; ---- moon.item ----
(defn item-valid? [item]
  (let [keyset (set (keys item))]
    (or (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot
             :stats/modifiers} keyset)
        (= #{:property/id
             :property/pretty-name
             :entity/image
             :item/slot} keyset))))

(defn item-info-text [item]
  (assert (item-valid? item))
  (str/join "\n"
            (remove nil?
                    [(str "[PRETTY_NAME]" (:property/pretty-name item) "[]")
                     (str "[LIME]" (str/capitalize (name (:item/slot item))) "[]")
                     ; seq because they can be empty map ?
                     (when (seq (:stats/modifiers item))
                       (str "[CYAN]" (mods-format-text (:stats/modifiers item)) "[]"))])))

;; ---- moon.inventory ----
(def slots
  #{:inventory.slot/bag
    :inventory.slot/weapon
    :inventory.slot/shield
    :inventory.slot/helm
    :inventory.slot/chest
    :inventory.slot/leg
    :inventory.slot/glove
    :inventory.slot/boot
    :inventory.slot/cloak
    :inventory.slot/necklace
    :inventory.slot/rings})

(defn- known-slot? [slot]
  (slots slot))

(defn- cells-and-items [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (for [[position item] (slot inventory)]
    [[slot position] item]))

(defn- free-cell [inventory slot]
  (assert (known-slot? slot) (str "Slot :" (pr-str slot)))
  (first (filter (fn [[_cell cell-item]]
                   (nil? cell-item))
                 (cells-and-items inventory slot))))

(defn can-pickup-item? [inventory item]
  (assert (item-valid? item))
  (or (free-cell inventory (:item/slot item))
      (free-cell inventory :inventory.slot/bag)))

(defn valid-slot? [[slot _] item]
  (or (= :inventory.slot/bag slot)
      (= (:item/slot item) slot)))

(defn applies-modifiers? [[slot _]]
  (not= :inventory.slot/bag slot))

;; ---- moon.position ----
; not using `for` because creates a lazy seq (slow)
(let [offsets [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]]]
  (defn get-8-neighbours [position]
    (mapv #(mapv + position %) offsets)))

(defn diagonal? [[x1 y1] [x2 y2]]
  (and (not= x1 x2)
       (not= y1 y2)))

;; ---- moon.g2d ----
(defprotocol G2d
  (height [_])
  (width [_])
  (cells [_])
  (posis [_]))

(defn get-cells [g2d int-positions]
  (into [] (keep g2d) int-positions))

(deftype VectorGrid [data]
  G2d
  (height [_]
    (count (data 0)))

  (width [_]
    (count data))

  (cells [_]
    (apply concat data))

  (posis [this]
    (for [x (range (width this))
          y (range (height this))]
      [x y]))

  clojure.lang.ILookup
  (valAt [this p]
    (-> data
        (nth (p 0) nil)
        (nth (p 1) nil)))

  clojure.lang.IFn
  (invoke [this p] (.valAt this p))

  clojure.lang.Seqable
  (seq [this]
    (map #(vector %1 %2) (posis this) (cells this)))

  clojure.lang.IPersistentCollection
  (equiv [this obj]
    (and (= VectorGrid (class obj))
         (= (.data ^VectorGrid obj) data)))

  clojure.lang.Associative
  (assoc [this p v]
    (VectorGrid. (assoc-in data p v)))
  (containsKey [this [x y]]
    (and (contains? data x)
         (contains? (data 0) y)))

  Object
  (hashCode [this] (.hashCode data))
  (equals [this obj]
    (and (= VectorGrid (class obj))
         (.equals (.data ^VectorGrid obj) data)))
  (toString [this]
    (str "width " (width this) ", height " (height this))))

(defn ->VectorGrid [data]
  (VectorGrid. data))

; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj
(defn g2d-create
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (->VectorGrid
   (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                       (range h)))
         (range w))))

;; ---- moon.content-grid ----
(defn content-grid-create [width height cell-size]
  {:grid (g2d-create
          (inc (int (/ width cell-size)))
          (inc (int (/ height cell-size)))
          (fn [idx]
            (atom {:idx idx
                   :entities #{}})))
   :cell-w cell-size
   :cell-h cell-size})

(defn update-entity!
  [{:keys [grid
           cell-w
           cell-h]}
   eid]
  (let [{:keys [moon.content-grid/content-cell
                entity/position]} @eid
        [x y] position
        new-cell (get grid [(int (/ x cell-w))
                            (int (/ y cell-h))])]
    (when-not (= content-cell new-cell)
      (swap! new-cell update :entities conj eid)
      (swap! eid assoc :moon.content-grid/content-cell new-cell)
      (when content-cell
        (swap! content-cell update :entities disj eid)))))

(defn remove-entity! [_ eid]
  (-> @eid
      :moon.content-grid/content-cell
      (swap! update :entities disj eid)))

(defn content-grid-active-entities
  [{:keys [grid]} center-entity]
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (get-8-neighbours idx)))
       (keep grid)
       (mapcat (comp :entities deref))))

;; ---- moon.property ----
(defn property-type [{:keys [property/id]}]
  (keyword "properties" (namespace id)))

(defn type->id-namespace [property-type]
  (keyword (name property-type)))

;; ---- moon.rand ----
(defn int-between
  "returns a random integer between lower and upper bounds inclusive."
  ([[lower upper]]
   (int-between lower upper))
  ([lower upper]
   (+ lower (rand-int (inc (- upper lower))))))

;; ---- moon.raycaster ----
(defn raycaster-blocked?
  [[bool-arr
    int-width
    int-height]
   [start-x start-y]
   [target-x target-y]]
  (RayCaster/rayBlocked (double start-x)
                        (double start-y)
                        (double target-x)
                        (double target-y)
                        int-width
                        int-height
                        bool-arr))

(defn line-of-sight?
  [raycaster source target]
  (not (raycaster-blocked? raycaster
                 ; 2 abstraction leakages:
                 ; we know internal of entity
                 ; we know internal of body
                 ; entity/position function
                 ; entity _HAS_ position -> HAS :Entity/body ALWAYS
                 ; => ENTITY DEFINE DATA SHAPE
                 ; => CREATURE/PROJECTILE/ITEM DEFINE
                 (:entity/position source)
                 (:entity/position target))))

;; ---- moon.rectangle ----
(defn rectangle-touched-tiles
  "x is leftmost point and y bottom most point in the rectangle."
  [{:keys [x y width height]}]
  {:pre [x y width height]}
  (let [x       (float x)
        y       (float y)
        width   (float width)
        height  (float height)
        l (int x)
        b (int y)
        r (int (+ x width))
        t (int (+ y height))]
    (set
     (if (or (> width 1) (> height 1))
       (for [x (range l (inc r))
             y (range b (inc t))]
         [x y])
       [[l b] [l t] [r b] [r t]]))))

;; ---- moon.scene2d.group ----
(defn scene2d-group-create []
  (Group.))

(defn find-actor [group actor-name]
  (.findActor ^Group group actor-name))

(defn get-children [group]
  (.getChildren ^Group group))

(defn add-actor! [group actor]
  (.addActor ^Group group ^Actor actor))

(defn clear-children! [group]
  (.clearChildren ^Group group))

;; ---- moon.scene2d.table ----
(defn- set-cell-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (.fillX ^Cell cell)
      :fill-y?    (.fillY ^Cell cell)
      :expand?    (.expand ^Cell cell)
      :expand-x?  (.expandX ^Cell cell)
      :expand-y?  (.expandY ^Cell cell)
      :bottom?    (.bottom ^Cell cell)
      :colspan    (.colspan ^Cell cell (int arg))
      :pad        (.pad ^Cell cell (float arg))
      :pad-top    (.padTop ^Cell cell (float arg))
      :pad-bottom (.padBottom ^Cell cell (float arg))
      :width      (.width ^Cell cell (float arg))
      :height     (.height ^Cell cell (float arg))
      :center?    (.center ^Cell cell)
      :right?     (.right ^Cell cell)
      :left?      (.left ^Cell cell))))

(defn add-cell! [table cell-declaration]
  (-> (.add ^Table table ^Actor (:actor cell-declaration))
      (set-cell-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (.add ^Table table ^Actor props-or-actor)))
    (.row ^Table table))
  table)

(def ^:private scene2d-table-set-opt-fns
  {:table/rows (fn [table rows]
                 (add-rows! table rows)
                 (.pack ^Layout table))
   :table/cell-defaults (fn [table defaults]
                          (set-cell-opts! (.defaults ^Table table) defaults))})

(defn set-opts! [table opts]
  (doseq [[k v] opts :when (scene2d-table-set-opt-fns k)]
    ((scene2d-table-set-opt-fns k) table v))
  table)

(defn scene2d-table-create [opts]
  (doto (Table.)
    (set-opts! opts)))

;; ---- moon.scene2d.window ----
(defn set-modal! [window modal?]
  (.setModal ^Window window modal?))

(defn add-close-button! [window skin]
  (add-cell! (.getTitleTable ^Window window)
             {:actor (doto (TextButton. "X" ^Skin skin)
                       (.addListener (proxy [ChangeListener] []
                                           (changed [_event _actor]
                                             (.remove ^Actor window)))))}))

(def ^:private scene2d-window-set-opt-fns
  {:window/add-close-button? (fn [window skin _]
                               (add-close-button! window skin))})

(defn scene2d-window-create [{:keys [title skin] :as opts}]
  (let [window (Window. ^String title ^Skin skin)]
    (set-opts! window opts)
    (doseq [[k v] opts :when (and (scene2d-window-set-opt-fns k) v)]
      ((scene2d-window-set-opt-fns k) window skin v))
    window))

(defn title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (.getParent ^Actor actor)]
      (when-let [p (.getParent ^Actor p)]
        (and (instance? Window p)
             (= (.getTitleLabel ^Window p) actor))))))

;; ---- moon.error-window ----
(defmacro with-err-str [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(defn error-window-create
  [{:keys [skin throwable]}]
  (let [label-text (binding [*print-level* 3]
                     (with-err-str (repl/pst throwable)))]
    (doto (scene2d-window-create {:title "Error"
                          :skin skin
                          :table/rows [[{:actor (Label. ^String label-text ^Skin skin)}]]
                          :window/add-close-button? true})
      (set-modal! true))))

;; ---- moon.skill ----
(defn skill-valid? [skill]
  (= #{:property/id
       :property/pretty-name
       :entity/image
       :skill/action-time-modifier-key
       :skill/action-time
       :skill/start-action-sound
       :skill/effects
       :skill/cooldown
       :skill/cost}
     (set (keys skill))))

;; ---- moon.string ----
(defn remove-newlines [s]
  (let [new-s (-> s
                  (str/replace "\n\n" "\n")
                  (str/replace #"^\n" "")
                  str/trim-newline)]
    (if (= (count new-s) (count s))
      s
      (remove-newlines new-s))))

;; ---- moon.textures ----
(defn- recursively-search [handle extensions]
  (loop [[handle & remaining] (.list ^FileHandle handle)
         result []]
    (cond (nil? handle)
          result

          (.isDirectory ^FileHandle handle)
          (recur (concat remaining (.list ^FileHandle handle)) result)

          (extensions (.extension ^FileHandle handle))
          (recur remaining (conj result (.path ^FileHandle handle)))

          :else
          (recur remaining result))))

(defn textures-create
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search (.internal ^Files files folder) extensions))
                 :let [file (.internal ^Files files path)
                       pixmap (Pixmap. ^FileHandle file)]]
             [path (Texture. ^TextureData (FileTextureData. ^FileHandle file
                                                            ^Pixmap pixmap
                                                            ^Pixmap$Format (Pixmap/.getFormat ^Pixmap pixmap)
                                                            false))])))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (TextureRegion. ^Texture texture (int x) (int y) (int w) (int h))
      (TextureRegion. ^Texture texture))))

;; ---- moon.throwable ----
(let [print-level 3
      print-depth 24]
  (defn pretty-pst [throwable]
    (binding [*print-level* print-level]
      (pretty-repl/pretty-pst throwable print-depth))))

;; ---- moon.tiled-map ----
(defn get-properties [tiled-map]
  (.getProperties ^TiledMap tiled-map))

(defn get-layers [tiled-map]
  (.getLayers ^TiledMap tiled-map))

(defn get-property [tiled-map k]
  (.get ^MapProperties (get-properties tiled-map) k))

(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (.get ^MapLayers (get-layers tiled-map) ^String layer-name)]
    (for [x (range (.getWidth ^TiledMapTileLayer layer))
          y (range (.getHeight ^TiledMapTileLayer layer))
          :let [position [x y]
                cell (.getCell ^TiledMapTileLayer layer (int x) (int y))]
          :when cell
          :let [value (.get ^MapProperties (.getProperties ^TiledMapTile (.getTile ^TiledMapTileLayer$Cell cell))
                            property-key)]
          :when value]
      [position value])))

(defn tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell ^TiledMapTileLayer layer (int x) (int y))]
      (let [value (.get ^MapProperties (.getProperties ^TiledMapTile (.getTile ^TiledMapTileLayer$Cell cell))
                        "movement")]
        (assert value
                (str "Value for :movement at position "
                     position " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (.get ^MapProperties (get-properties tiled-map) "height"))
                                                                    (position 1))]
                     " and layer " (.getName ^TiledMapTileLayer layer) " is undefined."))
        value))))

(defn movement-property-layers [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(.get ^MapProperties (.getProperties ^TiledMapTileLayer %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(.getName ^TiledMapTileLayer layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

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
  (let [region (.getTextureRegion ^TiledMapTile tile)
        x1 (+ x (* (.getOffsetX ^TiledMapTile tile) unit-scale))
        y1 (+ y (* (.getOffsetY ^TiledMapTile tile) unit-scale))
        x2 (+ x1 (* (.getRegionWidth ^TextureRegion region) unit-scale))
        y2 (+ y1 (* (.getRegionHeight ^TextureRegion region) unit-scale))
        u1 (.getU ^TextureRegion region)
        v1 (.getV2 ^TextureRegion region)
        u2 (.getU2 ^TextureRegion region)
        v2 (.getV ^TextureRegion region)
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
    (.draw ^Batch batch
           ^Texture (.getTexture ^TextureRegion region)
           ^floats verts
           (int 0)
           (int num-vertices))))

(defn- draw-tile-layer!
  [layer
   batch
   unit-scale
   view-bounds
   color-setter]
  (let [num-vertices 20
        vertices (float-array num-vertices)
        batch-color (.getColor ^Batch batch)
        layer-width (.getWidth ^TiledMapTileLayer layer)
        layer-height (.getHeight ^TiledMapTileLayer layer)
        layer-tile-width (* (.getTileWidth ^TiledMapTileLayer layer) unit-scale)
        layer-tile-height (* (.getTileHeight ^TiledMapTileLayer layer) unit-scale)
        layer-offset-x (* (.getRenderOffsetX ^TiledMapTileLayer layer) unit-scale)
        layer-offset-y (* (- (.getRenderOffsetY ^TiledMapTileLayer layer)) unit-scale)
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
            (when-let [cell (.getCell ^TiledMapTileLayer layer (int col) (int row))]
              (when-let [tile (.getTile ^TiledMapTileLayer$Cell cell)]
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
  (.setProjectionMatrix ^Batch batch (.combined ^OrthographicCamera camera))
  (.begin ^Batch batch)
  (let [width  (* (.viewportWidth ^OrthographicCamera camera) (.zoom ^OrthographicCamera camera))
        height (* (.viewportHeight ^OrthographicCamera camera) (.zoom ^OrthographicCamera camera))
        up (.up ^OrthographicCamera camera)
        w (+ (* width  (Math/abs (float (.y ^Vector3 up))))
             (* height (Math/abs (float (.x ^Vector3 up)))))
        h (+ (* height (Math/abs (float (.y ^Vector3 up))))
             (* width  (Math/abs (float (.x ^Vector3 up)))))
        pos (.position ^OrthographicCamera camera)
        view-bounds {:x (- (.x ^Vector3 pos) (/ w 2))
                     :y (- (.y ^Vector3 pos) (/ h 2))
                     :width w
                     :height h}]
    (doseq [layer (filter #(.isVisible ^TiledMapTileLayer %) (get-layers tiled-map))]
      (draw-tile-layer! layer
                        batch
                        world-unit-scale
                        view-bounds
                        color-setter)))
  (.end ^Batch batch))

;; ---- moon.timer ----
(defn timer-create [elapsed-time duration]
  {:pre [(>= duration 0)]}
  {:duration duration
   :stop-time (+ elapsed-time duration)})

(defn stopped? [elapsed-time {:keys [stop-time]}]
  (>= elapsed-time stop-time))

(defn timer-ratio [elapsed-time {:keys [duration stop-time] :as timer}]
  {:post [(<= 0 % 1)]}
  (if (stopped? elapsed-time timer)
    0
    ; min 1 because floating point math inaccuracies
    (min 1 (/ (- stop-time elapsed-time) duration))))

(defn increment [timer duration]
  (update timer :stop-time + duration))

;; ---- moon.v2 ----
(defn v2-add [v1 v2]
  (mapv + v1 v2))

(defn move [position {:keys [direction speed delta-time]}]
  (mapv #(+ %1 (* %2 speed delta-time)) position direction))

(defn scale [[x y] scalar]
  [(* x scalar)
   (* y scalar)])

(defn dot
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))

(defn crs
  "Calculates the 2D cross product between this and the given vector"
  [[this-x this-y] [x y]]
  (- (* this-x y)
     (* this-y x)))

(defn length [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))

(defn normalise [[x y :as v]]
  (let [len (length v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))

(defn normal-vectors [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])

(defn v2-direction [[sx sy] [tx ty]]
  (normalise [(- (float tx) (float sx))
              (- (float ty) (float sy))]))

(defn distance
  [[x1 y1]
   [x2 y2]]
  (let [x-d (- x2 x1)
        y-d (- y2 y1)]
    (math/sqrt (+ (* x-d x-d)
                  (* y-d y-d)))))

(defn angle-deg
  "Returns the angle in degrees of this vector relative to the given reference vector.
  Angles are towards the positive y-axis (counter-clockwise) in the `[0, 360-` range."
  [this reference]
  (let [angle (math/to-degrees
               (math/atan2 (crs reference this)
                           (dot reference this)))]
    (if (neg? angle)
      (+ angle 360)
      angle)))

(defn angle-from-vector
  "converts theta of Vector2 to angle from top (top is 0 degree, moving left is 90 degree etc.), counterclockwise"
  [v]
  (angle-deg v [0 1]))

(defn double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (v2-direction [start-x start-y]
                     [target-y target-y])
        [normal1 normal2] (normal-vectors v)
        normal1 (scale normal1 (/ path-w 2))
        normal2 (scale normal2 (/ path-w 2))
        start1  (v2-add [start-x  start-y]  normal1)
        start2  (v2-add [start-x  start-y]  normal2)
        target1 (v2-add [target-x target-y] normal1)
        target2 (v2-add [target-x target-y] normal2)]
    [start1 target1 start2 target2]))

;; ---- moon.body ----
(defn body-direction [entity other-entity]
  (v2-direction (:entity/position entity)
                (:entity/position other-entity)))

(defn start-point [entity target-entity]
  (v2-add (:entity/position entity)
          (scale (body-direction entity target-entity)
                    (/ (:entity/width entity) 2))))

(defn end-point [entity target-entity maxrange]
  (v2-add (start-point entity target-entity)
          (scale (body-direction entity target-entity)
                    maxrange)))

(defn in-range? [entity target-entity maxrange]
  (< (- (float (distance (:entity/position entity)
                            (:entity/position target-entity)))
        (float (/ (:entity/width entity)  2))
        (float (/ (:entity/width target-entity) 2)))
     (float maxrange)))

(defn rectangle
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (Rectangle. (float x) (float y) (float width) (float height))))

(defn overlaps? [entity other-entity]
  (.overlaps ^Rectangle (rectangle entity)
             ^Rectangle (rectangle other-entity)))

(defn body-touched-tiles
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (rectangle-touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))

;; ---- moon.grid ----
(defn nearest-entity [cell faction]
  (-> cell faction :eid))

(defn nearest-entity-distance [cell faction]
  (-> cell faction :distance))

(let [order (get-8-neighbours [0 0])
      diagonal? (fn [[^int x ^int y]]
                  (and (not (zero? x))
                       (not (zero? y))))]
  (def ^:private diagonal-check-indizes
    (into {} (for [[x y] (filter diagonal? order)]
               [(first (positions #(= % [x y]) order))
                (vec (positions #(some #{%} [[x 0] [0 y]])
                                order))]))))

(defn- not-allowed-diagonal? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))

(defn remove-not-allowed-diagonals [adjacent-cells]
  (remove nil?
          (map-indexed
           (fn [idx cell]
             (when-not (or (nil? cell)
                           (not-allowed-diagonal? idx adjacent-cells))
               cell))
           adjacent-cells)))

(defn cached-adjacent-cells [grid cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      get-8-neighbours
                      (get-cells grid))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn- potential-field-step [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance #(nearest-entity-distance % faction)
        nearest-entity-at #(nearest-entity % faction)
        marked? faction]
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (pf-blocked? adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (diagonal? (:position cell*)
                                                                   (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell assoc faction {:distance distance-value
                                          :eid (nearest-entity-at cell*)})
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))

(defn generate-potential-field
  [grid faction tiles->entities max-iterations]
  (let [entity-cell-seq (for [[tile eid] tiles->entities]
                          [eid (grid tile)])
        marked (map second entity-cell-seq)]
    (doseq [[eid cell] entity-cell-seq]
      (swap! cell assoc faction {:distance 0
                                 :eid eid}))
    (loop [marked-cells     marked
           new-marked-cells marked
           iterations 0]
      (if (= iterations max-iterations)
        marked-cells
        (let [new-marked (potential-field-step grid faction new-marked-cells)]
          (recur (concat marked-cells new-marked)
                 new-marked
                 (inc iterations)))))))

(defn entities [cells]
  (into #{} (mapcat :entities) cells))

(defn valid-position? [g2d {:keys [entity/z-order] :as body} entity-id]
  (assert (:entity/collides? body))
  (let [cells* (into [] (map deref) (get-cells g2d (body-touched-tiles body)))]
    (and (not-any? #(cell-blocked? % z-order) cells*)
         (->> cells*
              entities
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:entity/collides? other-entity)
                                 (overlaps? other-entity
                                                 body)))))))))

(defn try-move [grid body entity-id movement]
  (let [new-body (update body :entity/position move movement)]
    (when (valid-position? grid new-body entity-id)
      new-body)))

(defn grid-try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(defn grid-nearest-enemy [grid entity]
  (nearest-entity @(grid (mapv int (:entity/position entity)))
                    (enemy (:entity/faction entity))))

(defn grid-nearest-enemy-distance [grid entity]
  (nearest-entity-distance @(grid (mapv int (:entity/position entity)))
                               (enemy (:entity/faction entity))))

(defn body->occupied-cells
  [grid {:keys [entity/position
                entity/width
                entity/height]
         :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (get-cells grid (body-touched-tiles body))
    [(grid (mapv int position))]))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid @eid)]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc :entity/occupied-cells cells)))

(defn set-touched-cells! [grid eid]
  (let [cells (get-cells grid (body-touched-tiles @eid))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (:entity/occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (:entity/touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn filter-viable-cells [eid adjacent-cells]
  (remove-not-allowed-diagonals
   (mapv #(when-not (or (pf-blocked? @%)
                        (occupied-by-other? @% eid))
            %)
         adjacent-cells)))

(defn get-min-dist-cell [distance-to cells]
  (let [cells (filter distance-to cells)]
    (when (seq cells)
      (apply min-key distance-to cells))))

(defn viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells eid (cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))

(defn find-next-cell
  "returns {:target-entity eid} or {:target-cell cell}. Cell can be nil."
  [grid eid own-cell]
  (let [faction (enemy (:entity/faction @eid))
        distance-to #(nearest-entity-distance @% faction)
        nearest-entity-at #(nearest-entity @% faction)
        own-dist (distance-to own-cell)
        adjacent-cells (cached-adjacent-cells grid own-cell)]
    (if (and own-dist (zero? (float own-dist)))
      {:target-entity (nearest-entity-at own-cell)}
      (if-let [adjacent-cell (first (filter #(and (distance-to %)
                                                  (zero? (float (distance-to %))))
                                            adjacent-cells))]
        {:target-entity (nearest-entity-at adjacent-cell)}
        {:target-cell (let [cells (filter-viable-cells eid adjacent-cells)
                            min-key-cell (get-min-dist-cell distance-to cells)]
                        (cond
                          (not min-key-cell) ; red
                          own-cell

                          (not own-dist)
                          min-key-cell

                          (> (float (distance-to min-key-cell)) (float own-dist)) ; red
                          own-cell

                          (< (float (distance-to min-key-cell)) (float own-dist)) ; green
                          min-key-cell

                          (= (distance-to min-key-cell) own-dist) ; yellow
                          (or
                           (some #(viable-cell? grid distance-to own-dist eid %) cells)
                           own-cell)))}))))

(defn grid-point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(.contains ^Rectangle (rectangle @%) (float (first pos)) (float (second pos)))
            (:entities @cell))))

(defn grid-circle->entities [g2d {:keys [position radius] :as circle}]
  (let [[x y] position
        gdx-circle (Circle. (float x) (float y) (float radius))]
    (->> circle
         outer-rectangle
         rectangle-touched-tiles
         (get-cells g2d)
         (map deref)
         entities
         (filter #(Intersector/overlaps ^Circle gdx-circle
                                        ^Rectangle (rectangle @%))))))

(defn inside-cell? [grid entity cell]
  (let [cells (get-cells grid (body-touched-tiles entity))]
    (and (= 1 (count cells))
         (= cell (first cells)))))

(defn grid-find-direction [grid eid]
  (let [position (:entity/position @eid)
        own-cell (grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
    (cond
      target-entity
      (v2-direction position (:entity/position @target-entity))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (occupied-by-other? @own-cell eid))
        (when-not (inside-cell? grid @eid target-cell)
          (v2-direction position (:middle @target-cell)))))))

; Assumption: The map contains no not-allowed diagonal cells, diagonal wall cells where both
; adjacent cells are walls and blocked.
; (important for wavefront-expansion and field-following)
; * entities do not move to NADs (they remove them)
; * the potential field flows into diagonals, so they should be reachable too.
;
; TODO assert somewhere/at map load no NAD's and @ potential field init & remove from
; potential-field-following the removal of NAD's.

; TODO remove max pot field movement player screen + 10 tiles as of screen size
; => is coupled to max-steps & also
; to friendly units follow player distance

(defn grid-update!
  [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (let [entities (filter #(= (:entity/faction @%) faction)
                                               entities)]
                          (zipmap (map #(mapv int (:entity/position @%)) entities)
                                  entities))
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell dissoc faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))

;; ---- moon.val-max ----
(def val-max-schema
  (m/schema
   [:and
    [:vector {:min 2 :max 2} [:int {:min 0}]]
    [:fn {:error/fn (fn [{[^int v ^int mx] :value} _]
                      (when (< mx v)
                        (format "Expected max (%d) to be smaller than val (%d)" v mx)))}
     (fn [[^int a ^int b]] (<= a b))]]))

(defn- val-max-valid? [val-max]
  (m/validate val-max-schema val-max))

(defn to-pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

(defn val-max-ratio
  "If mx and v is 0, returns 0, otherwise (/ v mx)"
  [[^int v ^int mx]]
  {:pre [(val-max-valid? [v mx])]}
  (if (and (zero? v) (zero? mx))
    0
    (/ v mx)))

(defn apply-min [val-max modifiers modifier-k]
  (assert (val-max-valid? val-max) val-max)
  (let [val-max (update val-max 0 mods-get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [v (max v mx)]]
    (assert (val-max-valid? result) result)
    result))

(defn apply-max [val-max modifiers modifier-k]
  (assert (val-max-valid? val-max) val-max)
  (let [val-max (update val-max 1 mods-get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [(min v mx) mx]]
    (assert (val-max-valid? result) result)
    result))

;; ---- moon.schema ----
(declare malli-form)

(defn- create-map-schema* [ks k->malli-schema-form]
  (apply vector :map {:closed true}
         (for [k ks
               :let [k? (keyword? k)
                     schema-props (if k? nil (k 1))
                     k (if k? k (k 0))]]
           (do
            (assert (keyword? k))
            (assert (or (nil? schema-props) (map? schema-props)) (pr-str ks))
            [k schema-props (k->malli-schema-form k)]))))

(defn schema-create-map-schema [schemas ks]
  (create-map-schema* ks
                      (fn [k]
                        (malli-form (get schemas k) schemas))))

(defn malli-form [schema schemas]
  (case (first schema)
    :s/animation
    (schema-create-map-schema schemas
                       [:animation/frames
                        :animation/frame-duration
                        :animation/looping?])

    :s/boolean
    :boolean

    :s/enum
    (apply vector :enum (rest schema))

    :s/image
    (schema-create-map-schema schemas
                       [:image/file
                        [:image/bounds {:optional true}]])

    :s/map
    (schema-create-map-schema schemas (second schema))

    :s/number
    (case (second schema)
      :int     int?
      :nat-int nat-int?
      :any     number?
      :pos     pos?
      :pos-int pos-int?)

    :s/one-to-many
    [:set [:qualified-keyword {:namespace (type->id-namespace (second schema))}]]

    :s/one-to-one
    [:qualified-keyword {:namespace (type->id-namespace (second schema))}]

    :s/qualified-keyword
    (apply vector :qualified-keyword (rest schema))

    :s/some
    :some

    :s/sound
    :string

    :s/string
    :string

    :s/val-max
    val-max-schema

    :s/vector
    (apply vector :vector (rest schema))))

;; ---- moon.schemas ----
(defn schemas-validate [schemas k value]
  (-> (get schemas k)
      (malli-form schemas)
      m/schema
      (validate-humanize value)))

;; ---- moon.db ----
(defn db-create []
  (let [schemas (-> "config/schema.edn" io/resource slurp edn/read-string)
        properties (-> "config/properties.edn" io/resource slurp edn/read-string)]
    (assert (or (empty? properties)
                (apply distinct? (map :property/id properties))))
    (doseq [property properties]
      (schemas-validate schemas (property-type property) property))
    {:db/data (zipmap (map :property/id properties) properties)
     :db/schemas schemas}))

(defn get-raw [{:keys [db/data]} property-id]
  (assert (contains? data property-id))
  (get data property-id) )

; SCHEMA
(defmulti create-value (fn [[k] _v _db]
                         k))

(defmethod create-value :default
  [_ v _db]
  v)

; SCHEMAS
(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))

(defmethod create-value :s/map
  [_ v db]
  (build-values (:db/schemas db) v db))

(defmethod create-value :s/one-to-many
  [_ property-ids db]
  (set (map (fn [property-id]
              (build-values (:db/schemas db)
                            (get-raw db property-id)
                            db))
            property-ids)))

(defmethod create-value :s/one-to-one
  [_ property-id db]
  (build-values (:db/schemas db)
                (get-raw db property-id)
                db))

(defn build [{:keys [db/schemas] :as this} property-id]
  (build-values schemas
                (get-raw this property-id)
                this))

;; ---- moon.stats ----
(def ^:private non-val-max-stat-ks
  [:stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max hp modifiers :modifier/hp-max))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max mana modifiers :modifier/mana-max))

(defn not-enough-mana?
  [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn stats-get-value
  [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (mods-get-value base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))

(defn add-mods [stats mods]
  (update stats :stats/modifiers mods-add mods))

(defn remove-mods [stats mods]
  (update stats :stats/modifiers mods-remove mods))

(defn apply-action-speed-modifier [stats skill action-time]
  (/ action-time
     (or (stats-get-value stats (:skill/action-time-modifier-key skill))
         1)))

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))

(defn effective-armor-save [source-stats target-stats]
  (max (- (or (stats-get-value source-stats :stats/armor-save) 0)
          (or (stats-get-value target-stats :stats/armor-pierce) 0))
       0))

(defn melee-damage [{:keys [entity/stats]}]
  (let [strength (or (stats-get-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn stats-format-text
  [stats]
  (str/join "\n" (concat
                   ["*STATS*"
                    (str "Mana: " (get-mana stats))
                    (str "Hitpoints: " (get-hitpoints stats))]
                   (for [stat-k non-val-max-stat-ks]
                     (str (str/capitalize (name stat-k)) ": "
                          (stats-get-value stats stat-k))))))

;; ---- moon.viewport ----
(defn unproject [viewport [x y]]
  (let [v2 (.unproject ^Viewport viewport (Vector2. (float x) (float y)))]
    [(.x ^Vector2 v2) (.y ^Vector2 v2)]))

;; ---- moon.world ----
(defn register-eid! [world eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:world/id-counter world) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (:world/entity-ids world) assoc id eid))

  (assert (:entity/position @eid))
  (update-entity! (:world/content-grid world) eid)

  (assert (:entity/position @eid))
  (when (:entity/collides? @eid)
    (assert (valid-position? (:world/grid world) @eid (:entity/id @eid))))
  (set-touched-cells! (:world/grid world) eid)
  (when (:entity/collides? @eid)
    (set-occupied-cells! (:world/grid world) eid))
  nil)

(defn unregister-eid! [world eid]
  (let [id (:entity/id @eid)]
    (swap! (:world/entity-ids world) dissoc id)
    (remove-entity! (:world/content-grid world) eid)
    (remove-from-touched-cells! (:world/grid world) eid)
    (when (:entity/collides? @eid)
      (remove-from-occupied-cells! (:world/grid world) eid)))
  nil)

(defn relocate-eid! [world eid]
  (update-entity! (:world/content-grid world) eid)
  (remove-from-touched-cells! (:world/grid world) eid)
  (set-touched-cells! (:world/grid world) eid)
  (when (:entity/collides? @eid)
    (remove-from-occupied-cells! (:world/grid world) eid)
    (set-occupied-cells! (:world/grid world) eid))
  nil)

(defn world-try-move-solid-body [world body entity-id movement]
  (grid-try-move-solid-body (:world/grid world) body entity-id movement))

(defn world-nearest-enemy [world entity]
  (grid-nearest-enemy (:world/grid world) entity))

(defn world-nearest-enemy-distance [world entity]
  (grid-nearest-enemy-distance (:world/grid world) entity))

(defn world-find-direction [world eid]
  (grid-find-direction (:world/grid world) eid))

(defn world-point->entities [world position]
  (grid-point->entities (:world/grid world) position))

(defn world-circle->entities [world circle]
  (grid-circle->entities (:world/grid world) circle))

(defn- touched-tile-cells [world entity]
  (map deref (get-cells (:world/grid world) (body-touched-tiles entity))))

(defn entities-at-touched-tiles [world entity]
  (entities (touched-tile-cells world entity)))

(defn blocked-at-touched-tiles? [world entity z-order]
  (some #(cell-blocked? % z-order) (touched-tile-cells world entity)))

(defn world-active-entities [world center-entity]
  (content-grid-active-entities (:world/content-grid world) center-entity))

(defn entity-by-id [world id]
  (get @(:world/entity-ids world) id))

(defn destroyed-eids [world]
  (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids world))))

(defn update-potential-fields! [world pf-cache faction entities max-iterations]
  (grid-update! (:world/grid world) pf-cache faction entities max-iterations))

(defn raycaster-data [world]
  (let [grid (:world/grid world)
        width (width grid)
        height (height grid)
        cells (for [cell (map deref (cells grid))]
                [(:position cell) (boolean (blocks-vision? cell))])]
    {:width width :height height :cells cells}))

(defn cell-at [world [x y]]
  (when-let [cell-atom ((:world/grid world) [x y])]
    @cell-atom))

(defn cells-at [world tile-positions]
  (for [[x y] tile-positions
        :let [cell (cell-at world [x y])]
        :when cell]
    [[x y] cell]))

(defn- create-grid [tiled-map]
  (g2d-create (get-property tiled-map "width")
              (get-property tiled-map "height")
              (fn [position]
                (atom
                 (map->R
                  {:position position
                   :middle (mapv (partial + 0.5) position)
                   :movement (case (movement-property tiled-map position)
                                "none" :none
                                "air" :air
                                "all" :all)
                   :entities #{}
                   :occupied #{}})))))

(defn world-create [tiled-map]
  (let [width (get-property tiled-map "width")
        height (get-property tiled-map "height")]
    {:world/id-counter (atom 0)
     :world/entity-ids (atom {})
     :world/grid (create-grid tiled-map)
     :world/content-grid (content-grid-create width height 16)}))

;; ---- moon.game ----
; 1. step only use ctx bag in listener fns
; 2. step remove ctx bag and just bind state over the fns
; 3. pass capabilitites/receive libgdx capabilities as functions
(def schema
  (m/schema
   [:map {:closed true}
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/world :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/player-eid :some]
    [:ctx/paused? :some]
    [:ctx/show-potential-field-colors? :any]
    [:ctx/show-cell-entities? :boolean]
    [:ctx/show-cell-occupied? :boolean]
    [:ctx/show-body-bounds? :boolean]
    [:ctx/show-tile-grid? :boolean]]))

(q/defrecord Record [])

(q/defrecord EntityRecord [])

(def minimum-size 0.39)

(def max-delta 0.04)

(def level-fn vampire)

(def pausing? true)

(def state->pause-game?
  {:active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true})

(def factions-iterations
  {:good 15
   :evil 5})

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(def world-unit-scale (float (/ 48)))

(def state (atom nil))
(def audio (atom nil))
(def batch (atom nil))
(def unit-scale (atom 1))
(def cursors (atom nil))
(def default-font (atom nil))
(def world-viewport (atom nil))
(def shape-drawer (atom nil))
(def shape-drawer-texture (atom nil))
(def textures (atom nil))
(def skin (atom nil))
(def stage (atom nil))

(def z-orders
  [:z-order/on-ground
   :z-order/ground
   :z-order/flying
   :z-order/effect])

(defn affected-targets
  [active-entities raycaster entity]
  (->> active-entities
       (filter #(:entity/species @%))
       (filter #(line-of-sight? raycaster entity @%))
       (remove #(:entity/player? @%))))

(defn projectile-start-point [entity direction size]
  (v2-add (:entity/position entity)
          (scale direction
                    (+ (/ (:entity/width entity) 2) size 0.1))))

(defn- add-text-effect [entity elapsed-time text duration]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter increment duration))
           {:text text
            :counter (timer-create elapsed-time duration)})))

(defn effect-applicable?
  [[k v] effect-ctx]
  (case k
    :effects/audiovisual
    (:effect/target-position effect-ctx)

    :effects/projectile
    (:effect/target-direction effect-ctx)

    :effects/spawn
    (and (:entity/faction @(:effect/source effect-ctx))
         (:effect/target-position effect-ctx))

    :effects/target-all
    true

    :effects/target-entity
    (and (:effect/target effect-ctx)
         (seq (filter #(effect-applicable? % effect-ctx) (:entity-effects v))))

    :effects.target/audiovisual
    (:effect/target effect-ctx)

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (and target
           (= (:entity/faction @target)
              (enemy (:entity/faction @source)))))

    :effects.target/damage
    (and (:effect/target effect-ctx)
         #_(:stats/hp @target))

    :effects.target/kill
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))

    :effects.target/melee-damage
    (effect-applicable? [:effects.target/damage (melee-damage @(:effect/source effect-ctx))]
                        effect-ctx)

    :effects.target/spiderweb
    (:entity/stats @(:effect/target effect-ctx))

    :effects.target/stun
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))))

(defn effect-useful?
  [[k v] effect-ctx ray-blocked?]
  (case k
    :effects/audiovisual
    false

    :effects/projectile
    (let [{:keys [projectile/max-range] :as projectile} v
          source-p (:entity/position @(:effect/source effect-ctx))
          target-p (:entity/position @(:effect/target effect-ctx))]
      (and (not (let [[start1 target1 start2 target2] (double-ray-endpositions source-p
                                                                                 target-p
                                                                                 (:projectile/size projectile))]
                  (or
                   (ray-blocked? start1 target1)
                   (ray-blocked? start2 target2))))
           (< (distance source-p target-p)
              max-range)))

    :effects/target-all
    false

    :effects/target-entity
    (in-range? @(:effect/source effect-ctx)
                    @(:effect/target effect-ctx)
                    (:maxrange v))

    :effects.target/audiovisual
    false

    true))

(defn- skill-usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (not-enough-mana? (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(effect-applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))

(def info
  {:k->fn {:creature/level (fn [v _elapsed-time]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _elapsed-time]
                           (stats-format-text entity-stats))
           :effects.target/convert (fn [_ _elapsed-time]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _elapsed-time]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _elapsed-time]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _elapsed-time]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _elapsed-time]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _elapsed-time]
                                  (str "Stuns for " (readable duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _elapsed-time]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _elapsed-time]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter elapsed-time]
                                             (str "Remaining: " (readable (timer-ratio elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _elapsed-time]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _elapsed-time]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _elapsed-time]
                              (mods-format-text mods))
           :entity/skills (fn [skills _elapsed-time]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _elapsed-time]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} elapsed-time]
                                    (str "Spiderweb - remaining: " (readable (timer-ratio elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _elapsed-time]
                                   "Piercing")
           :property/pretty-name (fn [v _elapsed-time]
                                    v)
           :skill/cooling-down? (fn [counter elapsed-time]
                                   (str "Cooldown: " (readable (timer-ratio elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _elapsed-time]
                                 (str "Action-Time: " (readable v) " seconds"))
           :skill/action-time-modifier-key (fn [v _elapsed-time]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _elapsed-time]
                             (str "Cooldown: " (readable v) " seconds"))
           :skill/cost (fn [v _elapsed-time]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _elapsed-time]
                       (str "Range: " v " Meters."))}
   :k-order [:property/pretty-name
             :skill/action-time-modifier-key
             :skill/action-time
             :skill/cooldown
             :skill/cost
             :skill/effects
             :entity/species
             :creature/level
             :entity/stats
             :entity/delete-after-duration
             :projectile/piercing?
             :entity/projectile-collision
             :maxrange
             :entity-effects]
   :k->colors {:property/pretty-name "PRETTY_NAME"
               :stats/modifiers "CYAN"
               :maxrange "LIGHT_GRAY"
               :creature/level "GRAY"
               :projectile/piercing? "LIME"
               :skill/action-time-modifier-key "VIOLET"
               :skill/action-time "GOLD"
               :skill/cooldown "SKY"
               :skill/cost "CYAN"
               :entity/delete-after-duration "LIGHT_GRAY"
               :entity/faction "SLATE"
               :entity/fsm "YELLOW"
               :entity/species "LIGHT_GRAY"
               :entity/temp-modifier "LIGHT_GRAY"}})

(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v elapsed-time)))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v elapsed-time))))))
         (str/join "\n")
         remove-newlines)))

(defn- prepare-entity-geometry [entity]
  (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
    (assert position)
    (assert width)
    (assert height)
    (assert (>= width  (if collides? minimum-size 0)))
    (assert (>= height (if collides? minimum-size 0)))
    (assert (or (boolean? collides?) (nil? collides?)))
    (assert ((set z-orders) z-order))
    (assert (or (nil? rotation-angle)
                (<= 0 rotation-angle 360)))
    (assoc entity
           :entity/position (mapv float position)
           :entity/width (float width)
           :entity/height (float height)
           :entity/rotation-angle (or rotation-angle 0))))

(defn create-component
  [elapsed-time k v]
  (case k
    :entity/animation
    (let [{:keys [animation/frames
                  animation/frame-duration
                  animation/looping?
                  delete-after-stopped?]} v]
      (assert (not (and looping? delete-after-stopped?)))
      {:frames (vec frames)
       :frame-duration frame-duration
       :looping? looping?
       :cnt 0
       :maxcnt (* (count frames) (float frame-duration))
       :delete-after-stopped? delete-after-stopped?})

    :entity/delete-after-duration
    (timer-create elapsed-time v)

    :entity/projectile-collision
    (assoc v :already-hit-bodies #{})

    :entity/stats
    (-> v
        (update :stats/mana (fn [v] [v v]))
        (update :stats/hp   (fn [v] [v v])))

    v))

(defmulti create-entity-state
  (fn [[k _v] _eid _elapsed-time]
    k))

(defmethod create-entity-state :default
  [[_k v] _eid _elapsed-time]
  v)

(defmethod create-entity-state :active-skill
  [[_k [skill effect-ctx]] eid elapsed-time]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier (:entity/stats @eid) skill)
                 (timer-create elapsed-time))})

(defmethod create-entity-state :stunned
  [[_k duration] _eid elapsed-time]
  {:counter (timer-create elapsed-time duration)})

(defmethod create-entity-state :player-moving
  [[_k movement-vector] _eid _elapsed-time]
  {:movement-vector movement-vector})

(defmethod create-entity-state :npc-moving
  [[_k movement-vector] eid elapsed-time]
  {:movement-vector movement-vector
   :timer (timer-create elapsed-time
                        (* (stats-get-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})

(defmethod create-entity-state :player-item-on-cursor
  [[_k item] _eid _elapsed-time]
  {:item item})

(def fsms
  {:npc (fsm/fsm-inc
          [[:npc-sleeping
            :kill -> :npc-dead
            :stun -> :stunned
            :alert -> :npc-idle]
           [:npc-idle
            :kill -> :npc-dead
            :stun -> :stunned
            :start-action -> :active-skill
            :movement-direction -> :npc-moving]
           [:npc-moving
            :kill -> :npc-dead
            :stun -> :stunned
            :timer-finished -> :npc-idle]
           [:active-skill
            :kill -> :npc-dead
            :stun -> :stunned
            :action-done -> :npc-idle]
           [:stunned
            :kill -> :npc-dead
            :effect-wears-off -> :npc-idle]
           [:npc-dead]])
   :player (fsm/fsm-inc
            [[:player-idle
              :kill -> :player-dead
              :stun -> :stunned
              :start-action -> :active-skill
              :pickup-item -> :player-item-on-cursor
              :movement-input -> :player-moving]
             [:player-moving
              :kill -> :player-dead
              :stun -> :stunned
              :no-movement-input -> :player-idle]
             [:active-skill
              :kill -> :player-dead
              :stun -> :stunned
              :action-done -> :player-idle]
             [:stunned
              :kill -> :player-dead
              :effect-wears-off -> :player-idle]
             [:player-item-on-cursor
              :kill -> :player-dead
              :stun -> :stunned
              :drop-item -> :player-idle
              :dropped-item -> :player-idle]
             [:player-dead]])})

(defn- create-fsm
  [fsm initial-state]
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn- create-action-bar []
  (doto (scene2d-table-create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (HorizontalGroup.)
                                  (.space (float 2))
                                  (.pad (float 2))
                                  (.setName "moon.ui.action-bar.horizontal-group")
                                  (.setUserObject (doto (ButtonGroup.)
                                                    (.setMaxCheckCount (int 1))
                                                    (.setMinCheckCount (int 0)))))
                         :expand? true
                         :bottom? true}]]})
    (.setFillParent true)
    (.setName "moon.ui.action-bar")))

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (find-actor action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^Actor group)}))

(defn- action-bar-add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        {:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                        (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                     (* scale (.getRegionHeight ^TextureRegion texture-region)))))
                 (.addListener (TextTooltip. ^String tooltip-text ^Skin skin))
                 (.setUserObject skill-id))]
    (add-actor! horizontal-group button)
    (.add ^ButtonGroup button-group ^Button button)
    nil))

(defn- action-bar-remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (get horizontal-group skill-id)]
    (.remove ^Actor button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn- action-bar-selected-skill [action-bar]
  (when-let [skill-button (.getChecked ^ButtonGroup (:button-group (action-bar-get-data action-bar)))]
    (.getUserObject ^Actor skill-button)))

(defn- inventory-window-get-cell [inventory-window cell]
  (->> "inventory-cell-table"
       (#(find-actor inventory-window %))
       get-children
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-remove-item! [inventory-window cell]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn- set-item [entity cell item]
  (assert (and (nil? (get-in (:entity/inventory entity) cell))
               (valid-slot? cell item)))
  (cond-> (assoc-in entity (cons :entity/inventory cell) item)
    (applies-modifiers? cell)
    (update :entity/stats add-mods (:stats/modifiers item))))

(defn- remove-item [entity cell]
  (let [item (get-in (:entity/inventory entity) cell)]
    (assert item)
    (cond-> (assoc-in entity (cons :entity/inventory cell) nil)
      (applies-modifiers? cell)
      (update :entity/stats remove-mods (:stats/modifiers item)))))

(defn after-create-component
  [ui-set-skill! ui-set-item! elapsed-time eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid elapsed-time))
      nil)

    :entity/skills
    (do
      (swap! eid assoc :entity/skills nil)
      (doseq [{:keys [property/id] :as skill} v]
        (assert (not (contains? (:entity/skills @eid) id)))
        (swap! eid update :entity/skills assoc id skill)
        (when (:entity/player? @eid)
          (ui-set-skill! skill)))
      nil)

    :entity/inventory
    (do
      (swap! eid assoc :entity/inventory (->> #:inventory.slot{:bag      [6 4]
                                                              :weapon   [1 1]
                                                              :shield   [1 1]
                                                              :helm     [1 1]
                                                              :chest    [1 1]
                                                              :leg      [1 1]
                                                              :glove    [1 1]
                                                              :boot     [1 1]
                                                              :cloak    [1 1]
                                                              :necklace [1 1]
                                                              :rings    [2 1]}
                                                 (map (fn [[slot [width height]]]
                                                        [slot (g2d-create width height (constantly nil))]))
                                                 (into {})))
      (doseq [item v]
        (assert (item-valid? item))
        (let [[cell cell-item] (can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)
          (when (:entity/player? @eid)
            (ui-set-item! cell item))))
      nil)

    nil))

(defn- item-place-position [player-position world-mouse-position maxrange]
  (v2-add player-position
          (scale (v2-direction player-position world-mouse-position)
                    (min maxrange
                         (distance player-position world-mouse-position)))))

(defn- mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn- button?
  [actor]
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)
                            (= "inventory-cell" (.getName ^com.badlogic.gdx.scenes.scene2d.Actor (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)))
                            (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor (.getParent ^com.badlogic.gdx.scenes.scene2d.Actor actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (title-bar? actor)
      [:mouseover-actor/window-title-bar]

      (button? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))

(defn- spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (-> creature-property
      (assoc :entity/position position
             :entity/collides? true
             :entity/z-order (if (:entity/flying? creature-property)
                               :z-order/flying
                               :z-order/ground))
      (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
      (safe-merge components)))

(defn- spawn-effect [position components]
  (assoc components
         :entity/width 0.5
         :entity/height 0.5
         :entity/z-order :z-order/effect
         :entity/position position))

(defn- spawn-alert [position faction duration elapsed-time]
  (spawn-effect position
                {:entity/alert-friendlies-after-duration
                 {:counter (timer-create elapsed-time duration)
                  :faction faction}}))

(defn- spawn-item [position item]
  {:entity/position position
   :entity/width 0.75
   :entity/height 0.75
   :entity/z-order :z-order/on-ground
   :entity/image (:entity/image item)
   :entity/item item
   :entity/clickable {:type :clickable/item
                      :text (:property/pretty-name item)}})

(defn- spawn-line [{:keys [start end duration color thick?]}]
  (spawn-effect start
                {:entity/line-render {:thick? thick? :end end :color color}
                 :entity/delete-after-duration duration}))

(defn- spawn-projectile
  [{:keys [position direction faction]}
   {:keys [entity/image
           projectile/max-range
           projectile/speed
           entity-effects
           projectile/size
           projectile/piercing?]}]
  {:entity/position position
   :entity/width size
   :entity/height size
   :entity/z-order :z-order/flying
   :entity/rotation-angle (angle-from-vector direction)
   :entity/movement {:direction direction :speed speed}
   :entity/image image
   :entity/faction faction
   :entity/delete-after-duration (/ max-range speed)
   :entity/destroy-audiovisual :audiovisuals/hit-wall
   :entity/projectile-collision {:entity-effects entity-effects
                                :piercing? piercing?}})

;; CTX SIDE EFFECT

; -> used in handle-fsm-event
(defn- show-modal! [skin stage {:keys [title text button-text on-click]}]
  (assert (not (find-actor (.getRoot ^Stage stage) "moon.ui.modal-window")))
  (.addActor ^Stage stage
                    (doto (scene2d-window-create {:title title
                                          :skin skin
                                          :table/rows [[{:actor (Label. ^String text ^Skin skin)}]
                                                       [{:actor (doto (TextButton. button-text skin)
                                                                       (.addListener (proxy [ChangeListener] []
                                                                         (changed [_event _actor]
                                                                           (.remove ^com.badlogic.gdx.scenes.scene2d.Actor (find-actor (.getRoot ^Stage stage)
                                                                                              "moon.ui.modal-window"))
                                                                           (on-click)))))}]]})
                      (set-modal! true)
                      (.setName "moon.ui.modal-window")
                      (.setPosition ^com.badlogic.gdx.scenes.scene2d.Actor (/ (.getWorldWidth ^Viewport (.getViewport ^Stage stage)) 2) (float (* (.getWorldHeight ^Viewport (.getViewport ^Stage stage)) (/ 3 4))) (float Align/center)))))

(defn- ui-set-item! [ctx cell item]
  (let [skin @skin
        stage @stage
        textures @textures]
    (-> (.getRoot ^Stage stage)
        (find-actor "moon.ui.windows.inventory")
        (inventory-window-set-item! cell
                                    {:texture-region (texture-region textures (:entity/image item))
                                     :tooltip-text (item-info-text item)}
                                    skin))))

(defn- ui-set-skill! [ctx elapsed-time skill]
  (let [skin @skin
        stage @stage
        textures @textures]
    (-> (.getRoot ^Stage stage)
        (find-actor "moon.ui.action-bar")
        (action-bar-add-skill! {:skill-id (:property/id skill)
                                :texture-region (texture-region textures (:entity/image skill))
                                :tooltip-text (info-text skill elapsed-time)}
                               skin))))

(defn- audiovisual! [spawn-entity! db audio position audiovisual]
  (let [{:keys [tx/sound entity/animation]} (if (keyword? audiovisual)
                                             (build db audiovisual)
                                             audiovisual)]
    (play! audio sound)
    (spawn-entity! (spawn-effect position
                                 {:entity/animation (assoc animation :delete-after-stopped? true)}))))

; handle-fsm-event haengt an handle-effect
(defn handle-effect
  [[k v] effect-ctx world-mouse-position spawn-entity! audiovisual! apply-effects! handle-fsm-event!
   active-entities colors raycaster elapsed-time]
  (case k
    :effects/audiovisual
    (audiovisual! (:effect/target-position effect-ctx) v)

    :effects/projectile
    (let [source (:effect/source effect-ctx)]
      (spawn-entity! (spawn-projectile
                      {:position (projectile-start-point @source
                                                         (:effect/target-direction effect-ctx)
                                                         (:projectile/size v))
                       :direction (:effect/target-direction effect-ctx)
                       :faction (:entity/faction @source)}
                      v)))

    :effects/spawn
    (let [source (:effect/source effect-ctx)]
      (spawn-entity! (spawn-creature {:position (:effect/target-position effect-ctx)
                                      :creature-property v
                                      :components {:entity/fsm {:fsm :fsms/npc
                                                                :initial-state :npc-idle}
                                                   :entity/faction (:entity/faction @source)}})))

    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target (affected-targets active-entities raycaster source*)]
        (spawn-entity! (spawn-line
                        {:start (:entity/position source*)
                         :end (:entity/position @target)
                         :duration 0.05
                         :color (:colors/target-all-line colors)
                         :thick? true}))
        (apply-effects! {:effect/source source
                         :effect/target target}
                        (:entity-effects v))))

    :effects/target-entity
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)
          body        @source
          target-body @target
          {:keys [maxrange entity-effects]} v]
      (if (in-range? body target-body maxrange)
        (do (spawn-entity! (spawn-line
                            {:start (start-point body target-body)
                             :end (:entity/position target-body)
                             :duration 0.05
                             :color (:colors/target-entity-line colors)
                             :thick? true}))
            (apply-effects! effect-ctx entity-effects))
        (audiovisual! (end-point body target-body maxrange)
                      :audiovisuals/hit-ground)))

    :effects.target/audiovisual
    (audiovisual! (:entity/position @(:effect/target effect-ctx)) v)

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (swap! target assoc :entity/faction (:entity/faction @source))
      nil)

    :effects.target/damage
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)
          source* @source
          target* @target
          hp (get-hitpoints (:entity/stats target*))]
      (cond
       (zero? (hp 0))
       nil

       ; TODO find a better way
       (not (:entity/stats target*))
       nil

       (and (:entity/stats source*)
            (:entity/stats target*)
            (< (rand) (effective-armor-save (:entity/stats source*)
                                                  (:entity/stats target*))))
       (do (swap! target add-text-effect elapsed-time "[WHITE]ARMOR" 0.3)
           nil)

       :else
       (let [min-max (if (:entity/stats source*)  ; projectiles dont have ....
                       (:damage/min-max (calc-damage (:entity/stats source*)
                                                           (:entity/stats target*)
                                                           v))
                       (:damage/min-max v))
             dmg-amount (int-between min-max)
             new-hp-val (max (- (hp 0) dmg-amount)
                             0)
             dmg-text (str "[RED]" dmg-amount "[]")]
         (swap! target assoc-in [:entity/stats :stats/hp 0] new-hp-val)
         (swap! target add-text-effect elapsed-time dmg-text 0.3)
         (handle-fsm-event! target world-mouse-position (if (zero? new-hp-val) :kill :alert))
         (audiovisual! (:entity/position target*) :audiovisuals/damage))))

    :effects.target/kill
    (handle-fsm-event! (:effect/target effect-ctx) world-mouse-position :kill)

    :effects.target/melee-damage
    ; TODO AT EFFECT CREATION MAKE
    ; same @ applicable
    (handle-effect [:effects.target/damage (melee-damage @(:effect/source effect-ctx))]
                   effect-ctx
                   world-mouse-position
                   spawn-entity!
                   audiovisual!
                   apply-effects!
                   handle-fsm-event!
                   active-entities
                   colors
                   raycaster
                   elapsed-time)

    :effects.target/spiderweb
    (let [target (:effect/target effect-ctx)]
      ; TODO stacking? (if already has k ?) or reset counter ? (see string-effect too)
      (when-not (:entity/temp-modifier @target)
        (swap! target assoc :entity/temp-modifier {:modifiers spiderweb-modifiers
                                                   :counter (timer-create elapsed-time spiderweb-duration)})
        (swap! target update :entity/stats add-mods spiderweb-modifiers)
        nil))

    :effects.target/stun
    (handle-fsm-event! (:effect/target effect-ctx) world-mouse-position :stun v)))

(defn- toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (find-actor "moon.ui.windows.inventory"))]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor inventory)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (find-actor "player-message")
      (.setUserObject (atom {:text message :counter 0}))))

(def colors
  (let [outline-alpha 0.4]
    {:colors/mouseover-tile-air (float-bits [1 1 0 0.5])
     :colors/mouseover-tile-none (float-bits [1 0 0 0.5])
     :colors/debug-body-outline-collides (float-bits [1 1 1 1])
     :colors/debug-body-outline (float-bits [0.5 0.5 0.5 1])
     :colors/debug-body-outline-render-error (float-bits [1 0 0 1])
     :colors/debug-cell-entities (float-bits [1 0 0 0.6])
     :colors/debug-cell-occupied (float-bits [0 0 1 0.6])
     :colors/debug-potential-field (fn [ratio]
                                     (float-bits [ratio (- 1 ratio) ratio 0.6]))
     :colors/target-all-line (float-bits [1 0 0 0.75])
     :colors/target-all-render (float-bits [1 0 0 0.5])
     :colors/target-entity-line (float-bits [1 0 0 0.75])
     :colors/target-entity-in-range (float-bits [1 0 0 0.5])
     :colors/target-entity-not-in-range (float-bits [1 1 0 0.5])
     :colors/enemy-color (float-bits [1 0 0 outline-alpha])
     :colors/friendly-color (float-bits [0 1 0 outline-alpha])
     :colors/neutral-color (float-bits [1 1 1 outline-alpha])
     :colors/hp-bar (fn [ratio]
                      (let [ratio (float ratio)
                            color (cond
                                    (> ratio 0.75) :green
                                    (> ratio 0.5) :darkgreen
                                    (> ratio 0.25) :yellow
                                    :else :red)]
                        (color {:green (float-bits [0 0.8 0 1])
                                :darkgreen (float-bits [0 0.5 0 1])
                                :yellow (float-bits [0.5 0.5 0 1])
                                :red (float-bits [0.5 0 0 1])})))
     :colors/hp-bar-rect (float-bits [0 0 0 1])
     :colors/temp-modifier (float-bits [0.5 0.5 0.5 0.4])
     :colors/active-skill-circle (float-bits [1 1 1 0.125])
     :colors/active-skill-sector (float-bits [1 1 1 0.5])
     :colors/stunned (float-bits [1 1 1 0.6])
     :colors/explored-tile (float-bits [0.5 0.5 0.5 1])
     :colors/visible-tile (float-bits [1 1 1 1])
     :colors/invisible-tile (float-bits [0 0 0 1])
     :colors/droppable-item (float-bits [0 0.6 0 0.8 1])
     :colors/not-allowed-drop-item (float-bits [0.6 0 0 0.8 1])
     :colors/item-rect (float-bits [0.5 0.5 0.5 1])}))

(def controls
  {:zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory Input$Keys/I
   :toggle-entity-info Input$Keys/E})

(def controls-info
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "Leftmouse click - use skill/drop item on cursor"]))

(def help-menu-item
  {:label "Help"
   :items [{:label controls-info}]})

(def debug-flags-menu-item
  {:label "Debug"
   :items [{:label "Toggle show-tile-grid?"
            :on-click #(update % :ctx/show-tile-grid? not)}
           {:label "Toggle show-cell-entities?"
            :on-click #(update % :ctx/show-cell-entities? not)}
           {:label "Toggle show-cell-occupied?"
            :on-click #(update % :ctx/show-cell-occupied? not)}
           {:label "Toggle show-body-bounds?"
            :on-click #(update % :ctx/show-body-bounds? not)}
           {:label "Potential field colors: off"
            :on-click #(assoc % :ctx/show-potential-field-colors? nil)}
           {:label "Potential field colors: :good"
            :on-click #(assoc % :ctx/show-potential-field-colors? :good)}
           {:label "Potential field colors: :evil"
            :on-click #(assoc % :ctx/show-potential-field-colors? :evil)}]})

(def select-world-menu-item
  {:label "Select World"
   :items (for [[label world-fn] [["Vampire" vampire]]]
            {:label (str "Start " label)
             :on-click (fn [ctx]
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'game.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'game.create.world/step)
                                 ui stage
                                 stage (:ctx/stage actor)]
                             (rebuild-actors! ui ctx)
                             #_(Disposable/.dispose (:ctx/tiled-map ctx))
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn)))
                         ctx)})})

(def dev-menus
  [debug-flags-menu-item
   help-menu-item
   select-world-menu-item])

(def max-speed
  (/ minimum-size max-delta))

(def render-z-order
  (apply hash-map (interleave z-orders (range))))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-fn-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.circle ^ShapeDrawer shape-drawer x y radius))

(defn- draw-fn-ellipse [shape-drawer [x y] radius-x radius-y color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.ellipse ^ShapeDrawer shape-drawer x y radius-x radius-y))

(defn- draw-fn-filled-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledCircle ^ShapeDrawer shape-drawer (float x) (float y) (float radius)))

(defn- draw-fn-filled-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledRectangle ^ShapeDrawer shape-drawer (float x) (float y) (float w) (float h)))

(defn- draw-fn-line [shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.line ^ShapeDrawer shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn- draw-fn-grid [shape-drawer leftx bottomy gridw gridh cellw cellh color-float-bits]
  (let [w (* (float gridw) (float cellw))
        h (* (float gridh) (float cellh))
        topy (+ (float bottomy) (float h))
        rightx (+ (float leftx) (float w))]
    (doseq [idx (range (inc (float gridw)))
            :let [linex (+ (float leftx) (* (float idx) (float cellw)))]]
      (draw-fn-line shape-drawer [linex topy] [linex bottomy] color-float-bits))
    (doseq [idx (range (inc (float gridh)))
            :let [liney (+ (float bottomy) (* (float idx) (float cellh)))]]
      (draw-fn-line shape-drawer [leftx liney] [rightx liney] color-float-bits))))

(defn- draw-fn-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.rectangle ^ShapeDrawer shape-drawer x y w h))

(defn- draw-fn-sector [shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.sector ^ShapeDrawer shape-drawer center-x center-y radius start-radians radians))

(defn- draw-fn-text [batch default-font unit-scale {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float scale))]
    (.setScale ^BitmapFont$BitmapFontData font-data (* old-scale scale))
    (.draw ^BitmapFont font
           ^Batch batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight ^BitmapFont font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale ^BitmapFont$BitmapFontData font-data old-scale)))

(defn- draw-fn-texture-region [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= @unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * world-unit-scale))
                          dimensions)))]
    (if center?
      (Batch/.draw ^Batch batch
                   ^TextureRegion texture-region
                   (float (- (float x) (/ (float w) 2)))
                   (float (- (float y) (/ (float h) 2)))
                   (float (/ (float w) 2))
                   (float (/ (float h) 2))
                   (float w)
                   (float h)
                   (float 1)
                   (float 1)
                   (float (or rotation 0)))
      (.draw ^Batch batch
             ^TextureRegion texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))

(defn- draw-with-line-width! [shape-drawer width draw-body]
  (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* width old-line-width))
    (draw-body)
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width)))
(defn effect-render
  [[k v] effect-ctx shape-drawer active-entities colors raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (draw-fn-line shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (:colors/target-all-render colors))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (draw-fn-line shape-drawer
                      (start-point body target-body)
                      (end-point body target-body maxrange)
                      (if (in-range? body target-body maxrange)
                        (:colors/target-entity-in-range colors)
                        (:colors/target-entity-not-in-range colors)))))

    nil))

(defn tile-color-setter*
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(defn draw-component
  [shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position
   textures colors player elapsed-time active-entities raycaster
   entity k v]
  (case k
    :entity/clickable
    (let [{:keys [text]} v
          {:keys [entity/position entity/height entity/mouseover?]} entity]
      (when (and mouseover? text)
        (let [[x y] position]
          (draw-fn-text batch default-font unit-scale {:text text
                             :x x
                             :y (+ y (/ height 2))
                             :up? true}))))

    :player-item-on-cursor
    (let [{:keys [item]} v]
      (when-not mouseover-actor
        (draw-fn-texture-region batch unit-scale
                                (texture-region textures (:entity/image item))
                                (item-place-position (:entity/position entity)
                                                     world-mouse-position
                                                     (- (:entity/click-distance-tiles entity) 0.1))
                                {:center? true})))

    :entity/animation
    (let [{:keys [frames cnt frame-duration]} v
          image (frames (min (int (/ (float cnt) (float frame-duration)))
                             (dec (count frames))))]
      (draw-fn-texture-region batch unit-scale
                              (texture-region textures image)
                              (:entity/position entity)
                              {:center? true
                               :rotation (or (:entity/rotation-angle entity) 0)}))

    :entity/image
    (draw-fn-texture-region batch unit-scale
                            (texture-region textures v)
                            (:entity/position entity)
                            {:center? true
                             :rotation (or (:entity/rotation-angle entity) 0)})

    :entity/line-render
    (let [{:keys [thick? end color]} v
          position (:entity/position entity)]
      (if thick?
        (draw-with-line-width! shape-drawer 4 #(draw-fn-line shape-drawer position end color))
        (draw-fn-line shape-drawer position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          color (cond (= faction (enemy (:entity/faction player)))
                      (:colors/enemy-color colors)
                      (= faction (:entity/faction player))
                      (:colors/friendly-color colors)
                      :else
                      (:colors/neutral-color colors))]
      (draw-with-line-width! shape-drawer 5
                             #(draw-fn-ellipse shape-drawer position
                                                (/ width 2)
                                                (/ height 2)
                                                color)))

    :entity/stats
    (let [ratio (val-max-ratio (get-hitpoints (:entity/stats entity)))]
      (when (or (< ratio 1) (:entity/mouseover? entity))
        (let [{:keys [entity/position entity/width entity/height]} entity
              [x y] position
              x (- x (/ width  2))
              y (+ y (/ height 2))
              height (* 5 world-unit-scale)
              border (* 1 world-unit-scale)]
          (draw-fn-filled-rectangle shape-drawer x y width height (:colors/hp-bar-rect colors))
          (draw-fn-filled-rectangle shape-drawer
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    ((:colors/hp-bar colors) ratio)))))

    :entity/string-effect
    (let [{:keys [text]} v
          [x y] (:entity/position entity)]
      (draw-fn-text batch default-font unit-scale {:text text
                         :x x
                         :y (+ y
                               (/ (:entity/height entity) 2)
                               (* 5 world-unit-scale))
                         :scale 2
                         :up? true}))

    :entity/temp-modifier
    (draw-fn-filled-circle shape-drawer (:entity/position entity) 0.5 (:colors/temp-modifier colors))

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          {:keys [entity/image skill/effects]} skill
          radius active-skill-radius
          action-counter-ratio (timer-ratio elapsed-time counter)
          texture-region (texture-region textures image)
          [x y] (:entity/position entity)
          y (+ (float y)
               (float (/ (:entity/height entity) 2))
               (float 0.15))
          center [x (+ y radius)]]
      (draw-fn-filled-circle shape-drawer center radius (:colors/active-skill-circle colors))
      (draw-fn-sector shape-drawer
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (:colors/active-skill-sector colors))
      (draw-fn-texture-region batch unit-scale texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx shape-drawer
                       active-entities
                       colors
                       raycaster)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text batch default-font unit-scale {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (draw-fn-circle shape-drawer (:entity/position entity) 0.5 (:colors/stunned colors))))

(defn hp-mana-bar-create
  [ctx]
  (let [default-font @default-font
        stage @stage
        textures @textures
        {:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (.getWorldWidth ^Viewport (.getViewport ^Stage stage)) 2)
                    y-mana]
        rahmen-tex-reg (texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        draw-hpmana-bar! (fn [ctx batch x y content-file minmaxval name]
                           (draw-fn-texture-region batch unit-scale rahmen-tex-reg [x y])
                           (draw-fn-texture-region batch unit-scale
                                                   (texture-region textures
                                                                            {:image/file content-file
                                                                             :image/bounds [0 0 (* rahmenw (val-max-ratio minmaxval)) rahmenh]})
                                                   [x y])
                           (draw-fn-text batch default-font unit-scale {:text (str (readable (minmaxval 0))
                                                         "/"
                                                         (minmaxval 1)
                                                         " "
                                                         name)
                                              :x (+ x 75)
                                              :y (+ y 2)
                                              :up? true}))]
    (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
      (act [delta]
        (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
          (proxy-super act delta)))
      (draw [batch parent-alpha]
        (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
          (let [ctx (.ctx ^Stage stage)
                stats (:entity/stats @(:ctx/player-eid ctx))
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! ctx batch bar-x y-hp hpcontent-file (get-hitpoints stats) "HP")
            (draw-hpmana-bar! ctx batch bar-x y-mana manacontent-file (get-mana stats) "MP")))))))

(defn- ui-remove-item! [ctx cell]
  (-> (.getRoot ^Stage @stage)
      (find-actor "moon.ui.windows.inventory")
      (inventory-window-remove-item! cell)))

(defn handle-clicked-inventory-cell
  [player-eid audio handle-fsm-event! ui-set-item! ui-remove-item! cell world-mouse-position]
  (case (:state (:entity/fsm @player-eid))
    :player-idle
    (when-let [item (get-in (:entity/inventory @player-eid) cell)]
      (play! audio "bfxr_takeit")
      (swap! player-eid remove-item cell)
      (ui-remove-item! cell)
      (handle-fsm-event! player-eid world-mouse-position :pickup-item item))

    :player-item-on-cursor
    (let [entity @player-eid
          inventory (:entity/inventory entity)
          item-in-cell (get-in inventory cell)
          item-on-cursor (:entity/item-on-cursor entity)]
      (cond
       (and (not item-in-cell)
            (valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play! audio "bfxr_itemput")
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! player-eid world-mouse-position :dropped-item))

       (and item-in-cell
            (valid-slot? cell item-on-cursor))
       (do (swap! player-eid dissoc :entity/item-on-cursor)
           (play! audio "bfxr_itemput")
           (swap! player-eid remove-item cell)
           (ui-remove-item! cell)
           (swap! player-eid set-item cell item-on-cursor)
           (ui-set-item! cell item-on-cursor)
           (handle-fsm-event! player-eid world-mouse-position :dropped-item)
           (handle-fsm-event! player-eid world-mouse-position :pickup-item item-in-cell))))

    nil))

(defn- inventory-window-cell [on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (Stack.)]
       (run! #(add-actor! stack %)
             [(proxy [Widget] []
                (draw [batch parent-alpha]
                  (when-let [stage (.getStage ^Actor this)]
                    (let [ctx (.ctx ^Stage stage)]
                      (draw-cell-rect! ctx
                                       @(:ctx/player-eid ctx)
                                       (.getX ^Actor this)
                                       (.getY ^Actor this)
                                       (let [[ux uy] (unproject (.getViewport ^Stage stage)
                                                                         [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])
                                             local (.stageToLocalCoordinates ^Actor this
                                                                             (Vector2. (float ux) (float uy)))
                                             x (.x ^Vector2 local)
                                             y (.y ^Vector2 local)]
                                         (.hit ^Actor this (float x) (float y) true))
                                       (.getUserObject ^Actor (.getParent ^Actor this)))))))
              (doto (Image. ^Drawable background-drawable)
                (.setName "image-widget")
                (.setUserObject {:background-drawable background-drawable
                                      :cell-size cell-size}))])
       (doto stack
         (.addListener (proxy [ClickListener] []
                         (clicked [event _x _y]
                           (on-click-cell event cell))))
         (.setName "inventory-cell")
         (.setUserObject cell)))}))

(defn- inventory-window-build
  [{:keys [on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint ^Color (Color. 1 1 1 0.4))))
        ->cell (partial inventory-window-cell on-click-cell slot->drawable draw-cell-rect! cell-size)
        window (doto (scene2d-window-create {:title "Inventory"
                                     :skin skin
                                     :table/rows [[{:actor (doto (scene2d-table-create
                                                                  {:table/rows (concat [[nil nil
                                                                                        (->cell :inventory.slot/helm)
                                                                                        (->cell :inventory.slot/necklace)]
                                                                                       [nil
                                                                                        (->cell :inventory.slot/weapon)
                                                                                        (->cell :inventory.slot/chest)
                                                                                        (->cell :inventory.slot/cloak)
                                                                                        (->cell :inventory.slot/shield)]
                                                                                       [nil nil
                                                                                        (->cell :inventory.slot/leg)]
                                                                                       [nil
                                                                                        (->cell :inventory.slot/glove)
                                                                                        (->cell :inventory.slot/rings :position [0 0])
                                                                                        (->cell :inventory.slot/rings :position [1 0])
                                                                                        (->cell :inventory.slot/boot)]]
                                                                                      (for [y (range 4)]
                                                                                        (for [x (range 6)]
                                                                                          (->cell :inventory.slot/bag :position [x y]))))})
                                                                  (.setName "inventory-cell-table"))
                                                    :pad 4}]]})
                     (.setName "moon.ui.windows.inventory")
                     (.setVisible false))]
    (let [[x y] position]
      (.setPosition ^Actor window (float x) (float y)))
    window))

(defn inventory-window-create
  [ctx on-click-cell draw-cell-rect!]
  (let [skin @skin
        stage @stage
        textures @textures
        slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))
        cell-size 48]
    (inventory-window-build
     {:on-click-cell on-click-cell
      :draw-cell-rect! draw-cell-rect!
      :skin skin
      :position [(.getWorldWidth ^Viewport (.getViewport ^Stage stage))
                 (.getWorldHeight ^Viewport (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size cell-size})))

(defn windows-create [ctx actor-fns]
  (let [group* (scene2d-group-create)]
    (run! #(add-actor! group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (.setName "moon.ui.windows"))))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (Label. "MY LABEL TEXT" ^Skin skin)
        window (doto (scene2d-window-create {:title title
                                     :skin skin
                                     :table/rows [[{:actor label :expand? true}]]})
                 (.setName actor-name)
                 (.setVisible visible?))]
    (let [[x y] position]
      (.setPosition ^Actor window (float x) (float y)))
    (add-actor! window (proxy [Actor] []
                         (act [delta]
                           (when-let [stage (.getStage ^Actor this)]
                             (.setText ^Label label ^String (set-label-text! (.ctx ^Stage stage))))
                           (.pack ^Layout window)
                           (let [^Actor this this]
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
    window))

(defn stage-info-window-create
  [ctx]
  (let [skin @skin
        stage @stage]
    (create-info-window
     {:title "Entity Info"
      :actor-name "moon.ui.windows.entity-info"
      :visible? false
      :position [(.getWorldWidth ^Viewport (.getViewport ^Stage stage)) 0]
      :set-label-text! (fn [ctx]
                         (if-let [eid (:ctx/mouseover-eid ctx)]
                           (info-text (apply dissoc @eid [:entity/skills
                                                          :entity/faction
                                                          :active-skill])
                                      (:ctx/elapsed-time ctx))
                           ""))
      :skin skin})))

(defn entity-state-draw-ui-view
  [[k _v] eid ctx batch unit-scale mouseover-actor ui-mouse-position]
  (case k
    :player-item-on-cursor
    (when mouseover-actor
      (draw-fn-texture-region batch unit-scale
                              (texture-region @textures (:entity/image (:entity/item-on-cursor @eid)))
                              ui-mouse-position
                              {:center? true}))

    nil))

(defn player-state-draw-create [unit-scale]
  (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
    (act [delta]
      (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
            ctx (.ctx ^Stage stage)
            player-eid (:ctx/player-eid ctx)
            entity @player-eid
            state-k (:state (:entity/fsm entity))
            ui-mouse-position (unproject (.getViewport ^Stage stage)
                                                  [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])
            [x y] ui-mouse-position]
        (entity-state-draw-ui-view [state-k (state-k entity)]
                                   player-eid
                                   ctx
                                   batch
                                   unit-scale
                                   (mouseover-actor stage x y)
                                   ui-mouse-position)))))

(defn player-message-actor-create [default-font unit-scale]
  (let [message-duration-seconds 0.5]
    (doto (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
            (act [delta]
              (let [state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (let [ctx (.ctx ^Stage stage)
                      state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)
                      vp-width (.getWorldWidth ^Viewport (.getViewport ^Stage stage))
                      vp-height (.getWorldHeight ^Viewport (.getViewport ^Stage stage))]
                  (when-let [text (:text @state)]
                    (draw-fn-text batch default-font unit-scale {:x (/ vp-width 2)
                                       :y (+ (/ vp-height 2) 200)
                                       :text text
                                       :scale 2.5
                                       :up? true}))))))
      (.setName "player-message")
      (.setUserObject (atom nil)))))

(defn- interaction-state->txs [[k params] stage audio handle-fsm-event! ui-set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (find-actor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play! audio "bfxr_takeit")
                  (handle-fsm-event! player-eid world-mouse-position :pickup-item item))

              (can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play! audio "bfxr_pickup")
                  (assert (item-valid? item))
                  (let [[cell cell-item] (can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (ui-set-item! cell item))
                  nil)

              :else
              (do (play! audio "bfxr_denied")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play! audio "bfxr_denied")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play! audio "bfxr_denied")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play! audio "bfxr_denied")
        (show-message! stage "No selected skill")
        nil)))

(defn- handle-input
  [state-k eid ctx audio handle-fsm-event! left-button-pressed? movement-vector mouseover-actor world-mouse-position]
  (case state-k
    :player-idle
    (if movement-vector
      (handle-fsm-event! eid world-mouse-position :movement-input movement-vector)
      (when left-button-pressed?
        (interaction-state->txs (:ctx/interaction-state ctx)
                                @stage
                                audio
                                handle-fsm-event!
                                #(ui-set-item! ctx %1 %2)
                                eid
                                world-mouse-position)))

    :player-moving
    (if movement-vector
      (do (swap! eid assoc :entity/movement {:direction movement-vector
                                             :speed (or (stats-get-value (:entity/stats @eid) :stats/movement-speed)
                                                        0)})
          nil)
      (handle-fsm-event! eid world-mouse-position :no-movement-input))

    :player-item-on-cursor
    (when (and left-button-pressed?
               (not mouseover-actor))
      (handle-fsm-event! eid world-mouse-position :drop-item))

    nil))

(defn player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:entity/position @mouseover-eid))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v2-direction (:entity/position @player-eid)
                                         target-position)}))

(defn- choose-skill [ray-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill-usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect-applicable? e effect-ctx)))
                          (some (fn [e] (effect-useful? e effect-ctx ray-blocked?))))))
       first))

(defn- create-effect-ctx
  [ctx eid]
  (let [world (:ctx/world ctx)
        raycaster (:ctx/raycaster ctx)
        entity @eid
        target (world-nearest-enemy world entity)
        target (when (and target
                          (line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body-direction entity
                                                @target))}))

(defn- update-effect-ctx
  [raycaster effect-ctx]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    (if (and target
             (not (:entity/destroyed? @target))
             (line-of-sight? raycaster @source @target))
      effect-ctx
      (dissoc effect-ctx :effect/target))))

(defn tick-component
  [ctx world-mouse-position apply-effects! handle-fsm-event! eid [k v]]
  (case k
    :entity/animation
    (let [{:keys [delete-after-stopped?
                  looping?
                  cnt
                  maxcnt]
           :as animation} v]
      (swap! eid assoc :entity/animation (let [maxcnt (float maxcnt)
                                               newcnt (+ (float cnt) (float (:ctx/delta-time ctx)))]
                                           (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                       looping? (min maxcnt (- newcnt maxcnt))
                                                                       :else maxcnt))))
      (when (and delete-after-stopped?
                 (and (not looping?) (>= cnt maxcnt)))
        (swap! eid assoc :entity/destroyed? true))
      nil)

    :entity/alert-friendlies-after-duration
    (let [{:keys [counter faction]} v]
      (when (stopped? (:ctx/elapsed-time ctx) counter)
        (swap! eid assoc :entity/destroyed? true)
        (doseq [friendly-eid (->> {:position (:entity/position @eid)
                                   :radius 4}
                                  (world-circle->entities (:ctx/world ctx))
                                  (filter #(and (:entity/fsm @%)
                                                (not (:entity/destroyed? @%))
                                                (= (:entity/faction @%) faction))))]
          (handle-fsm-event! friendly-eid world-mouse-position :alert)))
      nil)

    :entity/string-effect
    (let [{:keys [counter]} v]
      (when (stopped? (:ctx/elapsed-time ctx) counter)
        (swap! eid dissoc :entity/string-effect))
      nil)

    :entity/skills
    (do (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
                :when (and cooling-down?
                           (stopped? (:ctx/elapsed-time ctx) cooling-down?))]
          (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
        nil)

    :entity/temp-modifier
    (let [{:keys [modifiers counter]} v]
      (when (stopped? (:ctx/elapsed-time ctx) counter)
        (swap! eid dissoc :entity/temp-modifier)
        (swap! eid update :entity/stats remove-mods modifiers))
      nil)

    :entity/projectile-collision
    (let [{:keys [entity-effects already-hit-bodies piercing?]} v
          world (:ctx/world ctx)
          entity @eid
          hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                          (not= (:entity/faction entity)
                                                (:entity/faction @%))
                                          (:entity/collides? @%)
                                          (overlaps? entity
                                                          @%))
                                    (entities-at-touched-tiles world entity)))
          destroy? (or (and hit-entity (not piercing?))
                       (blocked-at-touched-tiles? world entity (:entity/z-order entity)))]
      (when hit-entity
        (swap! eid assoc-in [:entity/projectile-collision :already-hit-bodies]
               (conj already-hit-bodies hit-entity)))
      (when destroy?
        (swap! eid assoc :entity/destroyed? true))
      (when hit-entity
        (apply-effects! {:effect/source eid
                         :effect/target hit-entity}
                        entity-effects))
      nil)

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          elapsed-time (:ctx/elapsed-time ctx)
          effect-ctx (update-effect-ctx (:ctx/raycaster ctx) effect-ctx)]
      (cond
       (not (seq (filter #(effect-applicable? % effect-ctx)
                         (:skill/effects skill))))
       (handle-fsm-event! eid world-mouse-position :action-done)

       (stopped? elapsed-time counter)
       (do (apply-effects! effect-ctx (:skill/effects skill))
           (handle-fsm-event! eid world-mouse-position :action-done)
           nil)))

    :entity/delete-after-duration
    (do (when (stopped? (:ctx/elapsed-time ctx) v)
          (swap! eid assoc :entity/destroyed? true))
        nil)

    :stunned
    (let [{:keys [counter]} v]
      (when (stopped? (:ctx/elapsed-time ctx) counter)
        (handle-fsm-event! eid world-mouse-position :effect-wears-off)))

    :npc-moving
    (let [{:keys [timer]} v]
      (when (stopped? (:ctx/elapsed-time ctx) timer)
        (handle-fsm-event! eid world-mouse-position :timer-finished)))

    :npc-sleeping
    (let [entity @eid]
      (when-let [distance (world-nearest-enemy-distance (:ctx/world ctx) entity)]
        (when (<= distance (stats-get-value (:entity/stats entity) :stats/aggro-range))
          (handle-fsm-event! eid world-mouse-position :alert))))

    :npc-idle
    (let [effect-ctx (create-effect-ctx ctx eid)]
      (if-let [skill (choose-skill (partial raycaster-blocked? (:ctx/raycaster ctx)) @eid effect-ctx)]
        (handle-fsm-event! eid world-mouse-position :start-action [skill effect-ctx])
        (handle-fsm-event! eid world-mouse-position :movement-direction (or (world-find-direction (:ctx/world ctx) eid)
                                                           [0 0]))))

    :entity/movement
    (let [{:keys [direction
                  speed
                  rotate-in-movement-direction?]
           :as movement} v]
      (assert (<= 0 speed max-speed)
              (pr-str speed))
      (assert (vector? direction))
      (assert (or (zero? (length direction))
                  (nearly-equal? 1 (length direction)))
              (str "cannot understand direction: " (pr-str direction)))
      (when-not (or (zero? (length direction))
                    (nil? speed)
                    (zero? speed))
        (let [world (:ctx/world ctx)
              movement (assoc movement :delta-time (:ctx/delta-time ctx))
              body @eid]
          (when-let [body (if (:entity/collides? body)
                             (world-try-move-solid-body world body (:entity/id @eid) movement)
                             (update body :entity/position move movement))]
            (swap! eid assoc :entity/position (:entity/position body))
            (when rotate-in-movement-direction?
              (swap! eid assoc :entity/rotation-angle
                     (angle-from-vector direction)))
            (relocate-eid! (:ctx/world ctx) eid)
            nil))))

    nil))

(defn- set-label-text-actor [label-widget text-fn]
  (proxy [Actor] []
    (act [delta]
      (when-let [stage (.getStage ^Actor this)]
        (.setText ^Label label-widget ^String (text-fn (.ctx ^Stage stage))))
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (scene2d-table-create {:table/rows [[{:actor (Image. ^Texture icon)}
                                                {:actor label}]]})]
     (add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor sub-table
                             :right? true
                             :expand-x? true})))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (add-actor! table (set-label-text-actor label text-fn))
     (add-cell! table {:actor label
                             :right? true
                             :expand-x? true}))))

(defn- dev-menu-main-table [skin menus update-labels]
  (let [table (scene2d-table-create {:table/rows [(for [{:keys [label items]} menus]
                                            {:actor
                                             (doto (TextButton. label skin)
                                               (.addListener (proxy [ChangeListener] []
                                                              (changed [event actor]
                                                                (.addActor ^Stage (.getStage ^Event event)
                                                                                  (scene2d-window-create {:title label
                                                                                                  :skin skin
                                                                                                  :table/rows [(for [{:keys [label on-click]} items]
                                                                                                                 {:actor
                                                                                                                  (doto (TextButton. label skin)
                                                                                                                    (.addListener (proxy [ChangeListener] []
                                                                                                                                   (changed [event actor]
                                                                                                                                     (let [stage (.getStage ^Event event)]
                                                                                                                                       (set! (.ctx ^Stage stage) (on-click (.ctx ^Stage stage))))))))})]
                                                                                                  :window/add-close-button? true}))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn- create-dev-menu
  [{:keys [menus update-labels skin]}]
  (doto (scene2d-table-create {:table/rows [[{:actor (dev-menu-main-table skin menus update-labels)
                                                           :expand-x? true
                                                           :fill-x? true
                                                           :colspan 1}]
                                                         [{:actor (doto (Label. "" ^Skin skin)
                                                                        (.setTouchable Touchable/disabled))
                                                           :expand? true
                                                           :fill-x? true
                                                           :fill-y? true}]]})
        (.setFillParent true)))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(defn- draw-tile-grid
  [ctx shape-drawer world-viewport]
  (when (:ctx/show-tile-grid? ctx)
    (let [[left-x _right-x bottom-y _top-y] (frustum (.getCamera ^Viewport world-viewport))]
      (draw-fn-grid shape-drawer
                     (int left-x)
                     (int bottom-y)
                     (inc (int (.getWorldWidth ^Viewport world-viewport)))
                     (+ 2 (int (.getWorldHeight ^Viewport world-viewport)))
                     1
                     1
                     (float-bits [1 1 1 0.8])))))

(defn- draw-cell-debug
  [ctx shape-drawer world-viewport]
  (let [world (:ctx/world ctx)
        tile-positions (visible-tiles (.getCamera ^Viewport world-viewport))]
    (doseq [[[x y] cell*] (cells-at world tile-positions)]
      (when (and (:ctx/show-cell-entities? ctx) (seq (:entities cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-entities colors)))
      (when (and (:ctx/show-cell-occupied? ctx) (seq (:occupied cell*)))
        (draw-fn-filled-rectangle shape-drawer x y 1 1 (:colors/debug-cell-occupied colors)))
      (when-let [faction (:ctx/show-potential-field-colors? ctx)]
        (let [{:keys [distance]} (faction cell*)]
          (when distance
            (let [ratio (/ distance (factions-iterations faction))]
              (draw-fn-filled-rectangle shape-drawer x y 1 1 ((:colors/debug-potential-field colors) ratio)))))))))

(defn draw-entity-rectangle!
  [ctx shape-drawer entity color-float-bits]
  (let [{:keys [entity/position entity/width entity/height]} entity
        [x y] [(- (position 0) (/ width 2))
               (- (position 1) (/ height 2))]]
    (draw-fn-rectangle shape-drawer x y width height color-float-bits)))

(defn- draw-entities!
  [ctx shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position]
  (let [player-eid (:ctx/player-eid ctx)
        raycaster (:ctx/raycaster ctx)
        textures @textures
        elapsed-time (:ctx/elapsed-time ctx)
        show-body-bounds? (:ctx/show-body-bounds? ctx)
        active-entities (:ctx/active-entities ctx)
        entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order (group-by :entity/z-order entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw-entity-rectangle! ctx shape-drawer
                                    entity
                                    (if (:entity/collides? entity)
                                      (:colors/debug-body-outline-collides colors)
                                      (:colors/debug-body-outline colors))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw-component shape-drawer batch default-font unit-scale mouseover-actor world-mouse-position
                            textures colors player elapsed-time active-entities raycaster
                            entity k v)))
        (catch Throwable t
          (draw-entity-rectangle! ctx shape-drawer
                                  entity
                                  (:colors/debug-body-outline-render-error colors))
          (pretty-pst t))))))

(defn- highlight-mouseover-tile
  [ctx shape-drawer world-mouse-position]
  (let [world (:ctx/world ctx)
        [x y] (mapv int world-mouse-position)
        cell (cell-at world [x y])]
    (when (and cell (#{:air :none} (:movement cell)))
      (draw-fn-rectangle shape-drawer x y 1 1
                         (case (:movement cell)
                           :air (:colors/mouseover-tile-air colors)
                           :none (:colors/mouseover-tile-none colors))))))

(defn- make-interaction-state
  [ctx mouseover-actor world-mouse-position]
  (let [player-eid (:ctx/player-eid ctx)
        mouseover-eid (:ctx/mouseover-eid ctx)
        stage @stage]
    (cond
      mouseover-actor
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (distance (:entity/position @player-eid)
                                        (:entity/position @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> (.getRoot ^Stage stage)
                            (find-actor "moon.ui.action-bar")
                            action-bar-selected-skill)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
              state (skill-usable-state skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn assoc-interaction-state [ctx mouseover-actor world-mouse-position]
  (assoc ctx :ctx/interaction-state (make-interaction-state ctx mouseover-actor world-mouse-position)))

(def k->cursor
  {:player-item-on-cursor :cursors/hand-grab
   :player-dead :cursors/black-x
   :active-skill :cursors/sandclock
   :stunned :cursors/denied
   :player-moving :cursors/walking
   :player-idle (fn
                  [eid ctx]
                  (let [[k params] (:ctx/interaction-state ctx)]
                    (case k
                      :interaction-state/mouseover-actor
                      (let [[actor-type params] params
                            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                                           (let [inventory-slot params]
                                                             (get-in (:entity/inventory @eid) inventory-slot)))]
                        (cond
                         inventory-cell-with-item?
                         :cursors/hand-before-grab

                         (= actor-type :mouseover-actor/window-title-bar)
                         :cursors/move-window

                         (= actor-type :mouseover-actor/button)
                         :cursors/over-button

                         (= actor-type :mouseover-actor/unspecified)
                         :cursors/default

                         :else
                         :cursors/default))

                      :interaction-state/clickable-mouseover-eid
                      (let [{:keys [clicked-eid
                                    in-click-range?]} params]
                        (case (:type (:entity/clickable @clicked-eid))
                          :clickable/item (if in-click-range?
                                            :cursors/hand-before-grab
                                            :cursors/hand-before-grab-gray)
                          :clickable/player :cursors/bag))

                      :interaction-state.skill/usable
                      :cursors/use-skill

                      :interaction-state.skill/not-usable
                      :cursors/skill-not-usable

                      :interaction-state/no-skill-selected
                      :cursors/no-skill-selected)))})

(defn- update-time [ctx]
  (let [delta-ms (min (.getDeltaTime ^Graphics Gdx/graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- update-potential-fields
  [ctx]
  (doseq [[faction max-iterations] factions-iterations]
    (update-potential-fields! (:ctx/world ctx)
                  (:ctx/potential-field-cache ctx)
                  faction
                  (:ctx/active-entities ctx)
                  max-iterations))
  ctx)

(def zoom-speed 0.025)

(defn update-draw-stage
  [ctx]
  (let [stage @stage]
    (set! (.ctx ^Stage stage) ctx)
    (.act ^Stage stage)
    (.draw ^Stage stage)
    (.ctx ^Stage stage)))

(defn- create-shape-drawer-texture []
  (let [pixmap (doto ^Pixmap (Pixmap. (int 1) (int 1) Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel (int 0) (int 0)))
        texture (Texture. ^TextureData (PixmapTextureData. ^Pixmap pixmap
                                                           ^Pixmap$Format (Pixmap/.getFormat ^Pixmap pixmap)
                                                           false
                                                           false))]
    (Disposable/.dispose pixmap)
    texture))

(defn spawn-entity! [entity]
  (let [ctx @state
        elapsed-time (:ctx/elapsed-time ctx)
        entity (reduce (fn [m [k v]]
                         (assoc m k (create-component elapsed-time k v)))
                       {}
                       entity)
        entity (prepare-entity-geometry entity)
        entity (merge (map->EntityRecord {}) entity)
        eid (atom entity)]
    (register-eid! (:ctx/world ctx) eid)
    (doseq [component @eid]
      (after-create-component #(ui-set-skill! ctx elapsed-time %)
                              #(ui-set-item! ctx %1 %2)
                              elapsed-time
                              eid
                              component))))

(defn handle-fsm-event! [eid world-mouse-position event & [params]]
  (let [ctx @state
        fsm (:entity/fsm @eid)
        _ (assert fsm)
        old-state-k (:state fsm)
        new-fsm (fsm/fsm-event fsm event)
        new-state-k (:state new-fsm)]
    (when-not (= old-state-k new-state-k)
      (let [old-state-obj (let [k (:state (:entity/fsm @eid))]
                             [k (k @eid)])
            state-args (if params [new-state-k params] [new-state-k nil])
            new-state-obj [new-state-k (create-entity-state state-args eid (:ctx/elapsed-time ctx))]]
        (swap! eid assoc :entity/fsm new-fsm)
        (swap! eid assoc new-state-k (new-state-obj 1))
        (swap! eid dissoc old-state-k)
        (let [[state-k _state-v] old-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [entity @eid
                  item (:entity/item-on-cursor entity)]
              (when item
                (swap! eid dissoc :entity/item-on-cursor)
                (play! @audio "bfxr_itemputground")
                (spawn-entity! (spawn-item (item-place-position (:entity/position entity)
                                                                    world-mouse-position
                                                                    (- (:entity/click-distance-tiles entity) 0.1))
                                               item))))

            :player-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            :npc-sleeping
            (do (swap! eid add-text-effect (:ctx/elapsed-time ctx) "[WHITE]!" 1)
                (spawn-entity! (spawn-alert (:entity/position @eid) (:entity/faction @eid) 0.2 (:ctx/elapsed-time ctx))))

            :npc-moving
            (do (swap! eid dissoc :entity/movement)
                nil)

            nil))
        (let [[state-k state-v] new-state-obj]
          (case state-k
            :player-item-on-cursor
            (let [{:keys [item]} state-v]
              (swap! eid assoc :entity/item-on-cursor item)
              nil)

            :active-skill
            (let [{:keys [skill]} state-v]
              (swap! eid update :entity/stats pay-mana-cost (:skill/cost skill))
              (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?]
                     (timer-create (:ctx/elapsed-time ctx) (:skill/cooldown skill)))
              (play! @audio (:skill/start-action-sound skill))
              nil)

            :npc-dead
            (do (swap! eid assoc :entity/destroyed? true)
                nil)

            :player-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats-get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            :player-dead
            (do (play! @audio "bfxr_playerdeath")
                (show-modal! @skin @stage {:title "YOU DIED - again!"
                                                               :text "Good luck next time!"
                                                               :button-text "OK"
                                                               :on-click (fn [])})
                nil)

            :npc-moving
            (let [{:keys [movement-vector]} state-v]
              (swap! eid assoc :entity/movement {:direction movement-vector
                                                 :speed (or (stats-get-value (:entity/stats @eid) :stats/movement-speed)
                                                            0)})
              nil)

            nil))
        nil))))

(defn create! [gdx-audio files input]
  (reset! audio (audio-create gdx-audio files))
  (reset! batch (SpriteBatch.))
  (reset! unit-scale 1)
  (reset! shape-drawer-texture (create-shape-drawer-texture))
  (reset! shape-drawer
          (ShapeDrawer. @batch
                        (TextureRegion. ^Texture @shape-drawer-texture (int 1) (int 0) (int 1) (int 1))))
  (reset! skin
          (let [s (Skin. ^FileHandle (.internal ^Files files "skin/uiskin.json"))]
            (set! (.markupEnabled ^BitmapFont$BitmapFontData
                                  (.getData (.getFont ^Skin s "default-font")))
                  true)
            s))
  (let [stage* (Stage. (FitViewport. (float 1440) (float 900)) @batch)]
    (.setInputProcessor ^Input input ^InputProcessor stage*)
    (reset! stage stage*))
  (set! (.initialTime ^TooltipManager (TooltipManager/getInstance)) 0)
  (Colors/put "PRETTY_NAME" (Color. 0.84 0.8 0.52 1))
  (reset! cursors
          (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
            (update-vals data
                         (fn [[path-segment [hotspot-x hotspot-y]]]
                           (let [path (format path-format path-segment)
                                 pixmap* (Pixmap. ^FileHandle (.internal ^Files files path))
                                 cursor (.newCursor ^Graphics Gdx/graphics ^Pixmap pixmap* hotspot-x hotspot-y)]
                             (Disposable/.dispose pixmap*)
                             cursor)))))
  (reset! textures
          (textures-create files {:folder "resources/"
                                  :extensions #{"png" "bmp"}}))
  (reset! world-viewport
          (let [world-width (* 1440 world-unit-scale)
                world-height (* 900 world-unit-scale)]
            (FitViewport. (float world-width)
                          (float world-height)
                          (doto (OrthographicCamera.)
                            (.setToOrtho false world-width world-height)))))
  (reset! default-font
          (let [{:keys [path
                        size
                        quality-scaling
                        use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                  :size 16
                                                  :quality-scaling 2
                                                  :use-integer-positions? false}
                generator (FreeTypeFontGenerator. ^FileHandle (.internal ^Files files path))
                parameter (let [p (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                            (set! (.size p) (* size quality-scaling))
                            (set! (.minFilter p) Texture$TextureFilter/Linear)
                            (set! (.magFilter p) Texture$TextureFilter/Linear)
                            p)
                font (.generateFont ^FreeTypeFontGenerator generator
                                    ^FreeTypeFontGenerator$FreeTypeFontParameter parameter)
                font-data (.getData ^BitmapFont font)]
            (Disposable/.dispose generator)
            (.setScale ^BitmapFont$BitmapFontData font-data (/ quality-scaling))
            (set! (.markupEnabled ^BitmapFont$BitmapFontData font-data) true)
            (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
            font))
  (reset! state
          (as-> {:ctx/active-entities nil
                 :ctx/delta-time nil
                 :ctx/mouseover-eid nil
                 :ctx/paused? false
                 :ctx/elapsed-time 0
                 :ctx/potential-field-cache (atom nil)
                 :ctx/show-potential-field-colors? nil
                 :ctx/show-cell-entities? false
                 :ctx/show-cell-occupied? false
                 :ctx/show-body-bounds? false
                 :ctx/show-tile-grid? false}
            ctx
            (merge (map->Record {}) ctx)
            (assoc ctx :ctx/db (db-create))
            (let [cell-size 48]
              (doseq [actor [(create-action-bar)
                             (create-dev-menu
                              {:menus dev-menus
                               :update-labels (for [item [{:label "elapsed-time"
                                                           :update-fn (fn [ctx]
                                                                        (str (readable (:ctx/elapsed-time ctx)) " seconds"))
                                                           :icon "images/clock.png"}
                                                          {:label "FPS"
                                                           :update-fn (fn [ctx] (.getFramesPerSecond ^Graphics Gdx/graphics))
                                                           :icon "images/fps.png"}
                                                          {:label "Mouseover-entity id"
                                                           :update-fn (fn [ctx]
                                                                        (when-let [entity (and (:ctx/mouseover-eid ctx) @(:ctx/mouseover-eid ctx))]
                                                                          (:entity/id entity)))
                                                           :icon "images/mouseover.png"}
                                                          {:label "paused?"
                                                           :update-fn :ctx/paused?}
                                                          {:label "GUI"
                                                           :update-fn (fn [ctx]
                                                                        (mapv int (unproject (.getViewport ^Stage @stage)
                                                                                                      [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])))}
                                                          {:label "World"
                                                           :update-fn (fn [ctx]
                                                                        (mapv int (unproject @world-viewport
                                                                                                      [(.getX ^Input Gdx/input) (.getY ^Input Gdx/input)])))}
                                                          {:label "Zoom"
                                                           :update-fn (fn [ctx]
                                                                        (.zoom ^OrthographicCamera (.getCamera ^Viewport @world-viewport)))
                                                           :icon "images/zoom.png"}]]
                                                (if (:icon item)
                                                  (update item :icon #(get @textures %))
                                                  item))
                               :skin @skin})
                             (hp-mana-bar-create ctx)
                             (windows-create ctx [stage-info-window-create
                                                  #(inventory-window-create
                                                    %
                                                    (fn [event cell]
                                                      (let [ctx (.ctx ^Stage (.getStage ^Event event))
                                                            world-mouse-position (unproject @world-viewport
                                                                                                    [(.getX ^Input Gdx/input)
                                                                                                     (.getY ^Input Gdx/input)])]
                                                        (handle-clicked-inventory-cell (:ctx/player-eid ctx)
                                                                                       @audio
                                                                                       handle-fsm-event!
                                                                                       (fn [cell item] (ui-set-item! ctx cell item))
                                                                                       (fn [cell] (ui-remove-item! ctx cell))
                                                                                       cell
                                                                                       world-mouse-position)))
                                                    (fn [ctx player-entity x y mouseover? cell]
                                                      (draw-fn-rectangle @shape-drawer x y cell-size cell-size (:colors/item-rect colors))
                                                      (when (and mouseover?
                                                                 (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                                                        (let [item (:entity/item-on-cursor player-entity)
                                                              color (if (valid-slot? cell item)
                                                                      (:colors/droppable-item colors)
                                                                      (:colors/not-allowed-drop-item colors))]
                                                          (draw-fn-filled-rectangle @shape-drawer (inc x) (inc y) (- cell-size 2) (- cell-size 2) color)))))])
                             (player-state-draw-create unit-scale)
                             (player-message-actor-create @default-font unit-scale)]]
                (.addActor ^Stage @stage actor))
              ctx)
            (let [{:keys [tiled-map start-position]}
                  (level-fn {})]
              (assoc ctx
                     :ctx/tiled-map tiled-map
                     :ctx/start-position start-position))
            (assoc ctx :ctx/world (world-create (:ctx/tiled-map ctx)))
            (assoc ctx :ctx/explored-tile-corners
                   (atom (g2d-create (get-property (:ctx/tiled-map ctx) "width")
                                          (get-property (:ctx/tiled-map ctx) "height")
                                          (constantly false))))
            (let [{:keys [width height cells]} (raycaster-data (:ctx/world ctx))
                  arr (make-array Boolean/TYPE width height)]
              (doseq [[[x y] blocked?] cells]
                (aset arr x y (boolean blocked?)))
              (assoc ctx :ctx/raycaster [arr width height]))
            (do
             (reset! state ctx)
             (spawn-entity! (spawn-creature {:position (mapv (partial + 0.5) (:ctx/start-position ctx))
                                             :creature-property (build (:ctx/db ctx) :creatures/vampire)
                                             :components {:entity/fsm {:fsm :fsms/player
                                                                       :initial-state :player-idle}
                                                          :entity/faction :good
                                                          :entity/player? true
                                                          :entity/free-skill-points 3
                                                          :entity/clickable {:type :clickable/player}
                                                          :entity/click-distance-tiles 1.5}}))
             ctx)
            (let [eid (entity-by-id (:ctx/world ctx) 1)]
              (assert (:entity/player? @eid))
              (assoc ctx :ctx/player-eid eid))
            (do
             (reset! state ctx)
             (let [start-position (:ctx/start-position ctx)]
               (doseq [[position creature-id] (spawn-positions (:ctx/tiled-map ctx))
                       :when (not= position start-position)]
                 (spawn-entity! (spawn-creature {:position (mapv (partial + 0.5) position)
                                                 :creature-property (build (:ctx/db ctx) (keyword creature-id))
                                                 :components {:entity/fsm {:fsm :fsms/npc
                                                                           :initial-state :npc-sleeping}
                                                              :entity/faction :evil}}))))
             ctx))))

(defn render! [mouse-position key-pressed? key-just-pressed? button-just-pressed?]
  (.glClearColor (.getGL20 ^Graphics Gdx/graphics) 0 0 0 0)
  (.glClear (.getGL20 ^Graphics Gdx/graphics) GL20/GL_COLOR_BUFFER_BIT)
  (swap! state #(or (.ctx ^Stage @stage) %))
  (validate-humanize schema @state)
  (let [default-font @default-font
        shape-drawer @shape-drawer
        ui-mouse-position (unproject (.getViewport ^Stage @stage) mouse-position)
        world-mouse-position (unproject @world-viewport mouse-position)]
    (swap! state (fn [ctx]
                   (let [player-eid (:ctx/player-eid ctx)
                         raycaster (:ctx/raycaster ctx)
                         mouseover-eid (:ctx/mouseover-eid ctx)
                         [x y] ui-mouse-position
                         new-eid (if (mouseover-actor @stage x y)
                                   nil
                                   (let [player @player-eid
                                         hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                                      (world-point->entities (:ctx/world ctx) world-mouse-position))]
                                     (->> render-z-order
                                          (sort-by-order hits #(:entity/z-order @%))
                                          reverse
                                          (filter #(line-of-sight? raycaster player @%))
                                          first)))]
                     (when mouseover-eid
                       (swap! mouseover-eid dissoc :entity/mouseover?))
                     (when new-eid
                       (swap! new-eid assoc :entity/mouseover? true))
                     (assoc ctx :ctx/mouseover-eid new-eid))))
    (swap! state #(assoc % :ctx/active-entities
                         (world-active-entities (:ctx/world %) @(:ctx/player-eid %))))
    (set-position! (.getCamera ^Viewport @world-viewport)
                                       (:entity/position @(:ctx/player-eid @state)))
    (let [ctx @state
          raycaster (:ctx/raycaster ctx)
          world-viewport @world-viewport
          explored-tile-corners (:ctx/explored-tile-corners ctx)
          tiled-map (:ctx/tiled-map ctx)]
      (draw! tiled-map
                            @batch
                            world-unit-scale
                            (.getCamera ^Viewport world-viewport)
                            (tile-color-setter*
                             {:ray-blocked? (partial raycaster-blocked? raycaster)
                              :explored-tile-corners explored-tile-corners
                              :light-position (position (.getCamera ^Viewport world-viewport))
                              :see-all-tiles? false
                              :explored-tile-color (:colors/explored-tile colors)
                              :visible-tile-color (:colors/visible-tile colors)
                              :invisible-tile-color (:colors/invisible-tile colors)})))
    (let [ctx @state
          world-viewport @world-viewport
          [x y] ui-mouse-position
          mouseover-actor* (mouseover-actor @stage x y)]
      (.setColor ^Batch @batch (float 1) (float 1) (float 1) (float 1))
      (.setProjectionMatrix ^Batch @batch (.combined ^OrthographicCamera (.getCamera ^Viewport world-viewport)))
      (.begin ^Batch @batch)
      (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
        (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* world-unit-scale old-line-width))
        (reset! unit-scale world-unit-scale)
        (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport)
                         #(draw-cell-debug % shape-drawer world-viewport)
                         #(draw-entities! % shape-drawer @batch default-font unit-scale mouseover-actor* world-mouse-position)
                         #(highlight-mouseover-tile % shape-drawer world-mouse-position)]]
          (draw-fn ctx))
        (reset! unit-scale 1)
        (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width))
      (.end ^Batch @batch)
      (swap! state assoc-interaction-state mouseover-actor* world-mouse-position)
      (let [ctx @state
            eid (:ctx/player-eid ctx)
            entity @eid
            state-k (:state (:entity/fsm entity))
            cursor-fn (k->cursor state-k)
            cursor-key (if (keyword? cursor-fn)
                         cursor-fn
                         (cursor-fn eid ctx))]
        (assert (contains? @cursors cursor-key))
        (.setCursor ^Graphics Gdx/graphics ^Cursor (get @cursors cursor-key)))
        (let [ctx @state
              eid (:ctx/player-eid ctx)
              entity @eid
              state-k (:state (:entity/fsm entity))
              movement-vector (let [r (when (key-pressed? Input$Keys/D) [1  0])
                                    l (when (key-pressed? Input$Keys/A) [-1 0])
                                    u (when (key-pressed? Input$Keys/W) [0  1])
                                    d (when (key-pressed? Input$Keys/S) [0 -1])]
                                (when (or r l u d)
                                  (let [v (normalise (reduce v2-add [0 0] (remove nil? [r l u d])))]
                                    (when (pos? (length v))
                                      v))))]
          (handle-input state-k eid ctx @audio handle-fsm-event!
                        (button-just-pressed? Input$Buttons/LEFT)
                        movement-vector
                        mouseover-actor*
                        world-mouse-position)))
    (swap! state dissoc :ctx/interaction-state)
    (swap! state (fn [ctx]
                   (assoc ctx :ctx/paused?
                          (or #_error
                              (and pausing?
                                   (state->pause-game? (:state (:entity/fsm @(:ctx/player-eid ctx))))
                                   (not (or (key-just-pressed? (:unpause-once controls))
                                            (key-pressed? (:unpause-continously controls)))))))))
    (when-not (:ctx/paused? @state)
      (swap! state #(-> % update-time update-potential-fields))
      (let [ctx @state
            audiovisual! (let [do-audiovisual! audiovisual!]
                           #(do-audiovisual! spawn-entity! (:ctx/db ctx) @audio %1 %2))
            active-entities (:ctx/active-entities ctx)
            raycaster (:ctx/raycaster ctx)
            elapsed-time (:ctx/elapsed-time ctx)]
        (try
          (letfn [(apply-effects! [effect-ctx effects]
                    (doseq [effect (filter #(effect-applicable? % effect-ctx) effects)]
                      (handle-effect effect effect-ctx world-mouse-position
                                     spawn-entity!
                                     audiovisual!
                                     apply-effects!
                                     handle-fsm-event!
                                     active-entities
                                     colors
                                     raycaster
                                     elapsed-time)))]
            (doseq [eid (:ctx/active-entities ctx)
                    component @eid]
              (try (tick-component ctx world-mouse-position
                                   apply-effects!
                                   handle-fsm-event!
                                   eid component)
                   (catch Throwable t
                     (throw (ex-info "Error at `entity/tick`:" {:eid eid} t))))))
          (catch Throwable t
            (pretty-pst t)
            (.addActor ^Stage @stage
                              (error-window-create
                               {:skin @skin
                                :throwable t}))))))
    (let [ctx @state]
      (doseq [eid (destroyed-eids (:ctx/world ctx))]
        (unregister-eid! (:ctx/world ctx) eid)
        (doseq [[k v] @eid]
          (case k
            :entity/destroy-audiovisual
            (audiovisual! spawn-entity!
                          (:ctx/db ctx)
                          @audio
                          (:entity/position @eid)
                          v)
            nil))))
    (let [ctx @state
          stage @stage
          world-viewport @world-viewport]
      (when (key-pressed? (:zoom-in controls))
        (inc-zoom! (.getCamera ^Viewport world-viewport) zoom-speed))
      (when (key-pressed? (:zoom-out controls))
        (inc-zoom! (.getCamera ^Viewport world-viewport) (- zoom-speed)))
      (when (key-just-pressed? (:close-windows-key controls))
        (->> (find-actor (.getRoot ^Stage stage) "moon.ui.windows")
             get-children
             (run! #(.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor % false))))
      (when (key-just-pressed? (:toggle-inventory controls))
        (toggle-inventory-visible! stage))
      (when (key-just-pressed? (:toggle-entity-info controls))
        (let [entity-info (find-actor (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
          (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info))))))
    (swap! state update-draw-stage)
    (validate-humanize schema @state)))

(def listener
  (reify ApplicationListener
    (create [_]
      (create! Gdx/audio Gdx/files Gdx/input))
    (dispose [_]
      (let [ctx @state]
        (audio-dispose! @audio)
        (Disposable/.dispose @batch)
        (run! Disposable/.dispose (vals @cursors))
        (Disposable/.dispose @default-font)
        (Disposable/.dispose @shape-drawer-texture)
        (Disposable/.dispose @skin)
        (run! Disposable/.dispose (vals @textures))
        (Disposable/.dispose (:ctx/tiled-map ctx))))
    (render [_]
      (let [input Gdx/input]
        (render! [(.getX ^Input input) (.getY ^Input input)]
                 #(.isKeyPressed ^Input input (int %))
                 #(.isKeyJustPressed ^Input input (int %))
                 #(.isButtonJustPressed ^Input input (int %)))))
    (resize [_ width height]
      (.update ^Viewport (.getViewport ^Stage @stage) width height true)
      (.update ^Viewport @world-viewport width height false))
    (pause [_])
    (resume [_])))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
