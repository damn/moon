(ns moon.application
  (:require [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.color :as color]
            [clojure.graphics.viewport :as viewport]
            [clojure.input :as gdx-input]
            [clojure.string :as str]
            [qrecord.core :as q]
            [moon.db :as db]
            [moon.draws :as draws]
            [moon.input :as input]
            [moon.malli :as m]
            [moon.start :refer [edn-resource]]
            [moon.txs :as txs]
            [moon.vector2 :as v]
            )
  (:import (com.badlogic.gdx ApplicationListener
                             Input)))

(defn- create-cursor
  [files
   graphics
   path-format
   [path [hotspot-x hotspot-y]]]
  (graphics/new-cursor graphics
                       (files/internal files (format path-format path))
                       hotspot-x
                       hotspot-y))

(extend-type Input
  input/Input
  (key-pressed? [input key]
    (gdx-input/key-pressed? input key))

  (key-just-pressed? [input key]
    (gdx-input/key-just-pressed? input key))

  (button-just-pressed? [input button]
    (gdx-input/button-just-pressed? input button))

  (mouse-position [input]
    (gdx-input/mouse-position input))

  (player-movement-vector [input]
    (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
          l (when (input/key-pressed? input :input.keys/a) [-1 0])
          u (when (input/key-pressed? input :input.keys/w) [0  1])
          d (when (input/key-pressed? input :input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color {:green     (color/float-bits [0 0.8 0 1])
            :darkgreen (color/float-bits [0 0.5 0 1])
            :yellow    (color/float-bits [0.5 0.5 0 1])
            :red       (color/float-bits [0.5 0 0 1])})))

(def txs-fn-map
  (update-vals '{
                 :tx/state-exit               moon.tx.state-exit/do!
                 :tx/audiovisual              moon.tx.audiovisual/do!
                 :tx/assoc                    moon.tx.assoc/do!
                 :tx/assoc-in                 moon.tx.assoc-in/do!
                 :tx/dissoc                   moon.tx.dissoc/do!
                 :tx/update                   moon.tx.update/do!
                 :tx/mark-destroyed           moon.tx.mark-destroyed/do!
                 :tx/set-cooldown             moon.tx.set-cooldown/do!
                 :tx/add-text-effect          moon.tx.add-text-effect/do!
                 :tx/add-skill                moon.tx.add-skill/do!
                 :tx/set-item                 moon.tx.set-item/do!
                 :tx/remove-item              moon.tx.remove-item/do!
                 :tx/pickup-item              moon.tx.pickup-item/do!
                 :tx/event                    moon.tx.event/do!
                 :tx/register-eid             moon.tx.register-eid/do!
                 :tx/unregister-eid           moon.tx.unregister-eid/do!
                 :tx/state-enter              moon.tx.state-enter/do!
                 :tx/effect                   moon.tx.effect/do!
                 :tx/spawn-alert              moon.tx.spawn-alert/do!
                 :tx/spawn-line               moon.tx.spawn-line/do!
                 :tx/move-entity              moon.tx.move-entity/do!
                 :tx/spawn-projectile         moon.tx.spawn-projectile/do!
                 :tx/spawn-effect             moon.tx.spawn-effect/do!
                 :tx/spawn-item               moon.tx.spawn-item/do!
                 :tx/spawn-creature           moon.tx.spawn-creature/do!
                 :tx/spawn-entity             moon.tx.spawn-entity/do!
                 :tx/sound                    moon.tx.nothing/do!
                 :tx/toggle-inventory-visible moon.tx.nothing/do!
                 :tx/show-message             moon.tx.nothing/do!
                 :tx/show-modal               moon.tx.nothing/do!
                 }
               requiring-resolve))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    moon.reaction-txs.sound/do!
                 :tx/toggle-inventory-visible moon.reaction-txs.toggle-inventory-visible/do!
                 :tx/show-message             moon.reaction-txs.show-message/do!
                 :tx/show-modal               moon.reaction-txs.show-modal/do!
                 :tx/set-item                 moon.reaction-txs.set-item/do!
                 :tx/remove-item              moon.reaction-txs.remove-item/do!
                 :tx/add-skill                moon.reaction-txs.add-skill/do!
                 }
               requiring-resolve))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some]
    [:ctx/schema :some]
    [:ctx/active-entities :any]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/colors :some]
    [:ctx/content-grid :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/cursors :some]
    [:ctx/db :some]
    [:ctx/default-font :some]
    [:ctx/delta-time :any]
    [:ctx/elapsed-time :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/files :some]
    [:ctx/graphics :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/input :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/mouseover-eid :any]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/render-z-order :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/start-position :some]
    [:ctx/textures :some]
    [:ctx/tiled-map :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/ui-viewport :some]
    [:ctx/unit-scale :some]
    [:ctx/world-mouse-position :any]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/z-orders :some]
    ]))

(defn- actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs
         handled-txs []]
    (if (empty? txs)
      handled-txs
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                _ (assert f (str "Cannot find function for tx: " k))
                new-txs (try
                         (apply f ctx params)
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur ctx
                   (concat (or new-txs []) (rest txs))
                   (conj handled-txs tx)))
          (recur ctx
                 (rest txs)
                 handled-txs))))))

(defn- reduce-actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs]
    (if (empty? txs)
      ctx
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                new-ctx (try
                         (if (nil? f)
                           ctx
                           (apply f ctx params))
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur new-ctx
                   (rest txs)))
          (recur ctx
                 (rest txs)))))))

(def draw-fns
  (update-vals '{
                 :draw/circle           moon.draw.circle/do!
                 :draw/ellipse          moon.draw.ellipse/do!
                 :draw/filled-circle    moon.draw.filled-circle/do!
                 :draw/filled-rectangle moon.draw.filled-rectangle/do!
                 :draw/grid             moon.draw.grid/do!
                 :draw/line             moon.draw.line/do!
                 :draw/rectangle        moon.draw.rectangle/do!
                 :draw/sector           moon.draw.sector/do!
                 :draw/text             moon.draw.text/do!
                 :draw/texture-region   moon.draw.texture-region/do!
                 :draw/with-line-width  moon.draw.with-line-width/do!
                 }
               requiring-resolve))

(q/defrecord Context []
  txs/TransactionHandler
  (handle! [ctx txs]
    (let [handled-txs (try (actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs)))

  draws/Draws
  (handle [ctx draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) ctx (rest component))))
  )

(defn- load-colors []
  {
   :colors/mouseover-tile-air  (color/float-bits [1 1 0 0.5])
   :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
   :colors/debug-body-outline-collides (color/float-bits white)
   :colors/debug-body-outline (color/float-bits gray)
   :colors/debug-body-outline-render-error (color/float-bits red)
   :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
   :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
   :colors/debug-potential-field (fn [ratio]
                                   (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
   :colors/target-all-line (color/float-bits [1 0 0 0.75])
   :colors/target-all-render (color/float-bits [1 0 0 0.5])
   :colors/target-entity-line (color/float-bits [1 0 0 0.75])
   :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
   :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
   :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
   :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
   :colors/neutral-color  (color/float-bits [1 1 1 outline-alpha])
   :colors/hp-bar hpbar-color
   :colors/hp-bar-rect (color/float-bits black)
   :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
   :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
   :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
   :colors/stunned (color/float-bits [1 1 1 0.6])
   :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
   :colors/visible-tile (color/float-bits [1 1 1 1])
   :colors/invisible-tile (color/float-bits [0 0 0 1])
   :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
   :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
   :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])
   }
  )

(defn- create!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (merge (map->Context {})
                 (let [batch (sprite-batch/create)
                       graphics (gdx/graphics)
                       files (gdx/files)
                       ui-viewport (fit-viewport/create 1440 900 (orthographic-camera/create))
                       stage (stage/create ui-viewport batch)
                       input (gdx/input)
                       ]
                   (gdx-input/set-processor! input stage)
                   {
                    :ctx/schema schema
                    :ctx/app      (gdx/app)
                    :ctx/audio    (gdx/audio)
                    :ctx/graphics  graphics
                    :ctx/files     files
                    :ctx/input     input
                    :ctx/batch batch
                    :ctx/ui-viewport ui-viewport
                    :ctx/stage stage
                    :ctx/skin (let [skin (skin/create (files/internal files "uiskin.json"))]
                                (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                                skin)

                    :ctx/cursors (let [{:keys [data path-format]} (edn-resource "cursors.edn")]
                                   (update-vals data (partial create-cursor files graphics path-format)))

                    :ctx/textures (let [{:keys [folder extensions]} {:folder "resources/"
                                                                     :extensions #{"png" "bmp"}}]
                                    (into {} (for [path (map (fn [path]
                                                               (str/replace-first path folder ""))
                                                             (file-handle/recursively-search (files/internal files folder) extensions))]
                                               [path (texture/create path)])))

                    :ctx/controls {
                                   :zoom-in :input.keys/minus
                                   :zoom-out :input.keys/equals
                                   :unpause-once :input.keys/p
                                   :unpause-continously :input.keys/space
                                   :close-windows-key :input.keys/escape
                                   :toggle-inventory  :input.keys/i
                                   :toggle-entity-info :input.keys/e
                                   :open-debug-button :input.buttons/right
                                   }
                    :ctx/controls-info (str/join "\n"
                                                 ["[W][A][S][D] - Move"
                                                  "[ESCAPE] - Close windows"
                                                  "[I] - Inventory window"
                                                  "[E] - Entity Info window"
                                                  "[-]/[=] - Zoom"
                                                  "[P]/[SPACE] - Unpause"
                                                  "rightclick on tile or entity - open debug data window"
                                                  "Leftmouse click - use skill/drop item on cursor"])
                    :ctx/colors (load-colors)
                    :ctx/db (db/create {:schemas "schema.edn"
                                        :properties "properties.edn"})
                    :ctx/active-entities nil
                    :ctx/delta-time nil
                    :ctx/mouseover-eid nil
                    :ctx/ui-mouse-position nil
                    :ctx/world-mouse-position nil
                    :ctx/elapsed-time 0
                    :ctx/paused? false
                    :ctx/unit-scale (atom 1)
                    :ctx/factions-iterations {:good 15 :evil 5}
                    :ctx/max-delta 0.04
                    :ctx/minimum-size 0.39
                    :ctx/z-orders [:z-order/on-ground
                                   :z-order/ground
                                   :z-order/flying
                                   :z-order/effect]
                    }))
          create-fns))

(defn-  dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose! (vals audio))
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map)
  nil)

(defn- render! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))

(defn- resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)

(def state (atom nil))

(defn start! [{:keys [colors listener config]}]
  (colors/put! colors)
  (config/use-glfw-async!)
  (lwjgl/application! (let [{:keys [create-params
                                    render-params]} listener]
                        (reify ApplicationListener
                          (create [_]
                            (tooltip-manager/set-initial-time! 0)
                            (reset! state (create! create-params)))

                          (dispose [_]
                            (dispose! @state))

                          (render [_]
                            (swap! state render! render-params))

                          (resize [_ width height]
                            (resize! @state width height))

                          (pause [_])

                          (resume [_])))
                      (config/create config)))
