(ns moon.create.record
  (:require [moon.tx-handler :as tx-handler]
            [moon.txs :as txs]
            [qrecord.core :as q]))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    moon.tx.sound/do!
                 :tx/toggle-inventory-visible moon.tx.toggle-inventory-visible/do!
                 :tx/show-message             moon.tx.show-message/do!
                 :tx/show-modal               moon.tx.show-modal/do!
                 :tx/set-item                 moon.tx.set-item/do!
                 :tx/remove-item              moon.tx.remove-item/do!
                 :tx/add-skill                moon.tx.add-skill/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 ;; FIXME only this passes ctx, otherwise 'world' only
                 :tx/state-exit               moon.world.tx.state-exit/do!
                 :tx/audiovisual              moon.world.tx.audiovisual/do!
                 ;;

                 :tx/assoc                    moon.world.tx.assoc/do!
                 :tx/assoc-in                 moon.world.tx.assoc-in/do!
                 :tx/dissoc                   moon.world.tx.dissoc/do!
                 :tx/update                   moon.world.tx.update/do!
                 :tx/mark-destroyed           moon.world.tx.mark-destroyed/do!
                 :tx/set-cooldown             moon.world.tx.set-cooldown/do!
                 :tx/add-text-effect          moon.world.tx.add-text-effect/do!
                 :tx/add-skill                moon.world.tx.add-skill/do!
                 :tx/set-item                 moon.world.tx.set-item/do!
                 :tx/remove-item              moon.world.tx.remove-item/do!
                 :tx/pickup-item              moon.world.tx.pickup-item/do!
                 :tx/event                    moon.world.tx.event/do!
                 :tx/state-enter              moon.world.tx.state-enter/do!
                 :tx/effect                   moon.world.tx.effect/do!
                 :tx/spawn-alert              moon.world.tx.spawn-alert/do!
                 :tx/spawn-line               moon.world.tx.spawn-line/do!
                 :tx/move-entity              moon.world.tx.move-entity/do!
                 :tx/spawn-projectile         moon.world.tx.spawn-projectile/do!
                 :tx/spawn-effect             moon.world.tx.spawn-effect/do!
                 :tx/spawn-item               moon.world.tx.spawn-item/do!
                 :tx/spawn-creature           moon.world.tx.spawn-creature/do!
                 :tx/spawn-entity             moon.world.tx.spawn-entity/do!
                 :tx/sound                    moon.world.tx.nothing/do!
                 :tx/toggle-inventory-visible moon.world.tx.nothing/do!
                 :tx/show-message             moon.world.tx.nothing/do!
                 :tx/show-modal               moon.world.tx.nothing/do!
                 }
               requiring-resolve))

(defn reduce-actions!
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
    (let [handled-txs (try (tx-handler/actions! txs-fn-map ctx txs)
                           (catch Throwable t
                             (throw (ex-info "Error handling txs"
                                             {:txs txs} t))))]
      (reduce-actions! reaction-txs-fn-map
                       ctx
                       handled-txs))))

(defn step [ctx]
  (merge (map->Context {}) ctx))
