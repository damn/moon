(ns moon.application
  (:require [clojure.disposable :as disposable]
            [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.graphics.viewport :as viewport]
            [qrecord.core :as q]
            [moon.malli :as m]
            [moon.txs :as txs]
            )
  (:import (com.badlogic.gdx ApplicationListener)))

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
  )

(defn- create!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (merge (map->Context {})
                 (let [batch (sprite-batch/create)]
                   {
                    :ctx/schema schema
                    :ctx/app      (gdx/app)
                    :ctx/audio    (gdx/audio)
                    :ctx/graphics (gdx/graphics)
                    :ctx/files    (gdx/files)
                    :ctx/input    (gdx/input)
                    :ctx/batch batch

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
