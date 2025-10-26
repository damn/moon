(ns cdq.create.record
  (:require [moon.tx-handler :as tx-handler]
            [moon.txs :as txs]
            [qrecord.core :as q]))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    cdq.tx.sound/do!
                 :tx/toggle-inventory-visible cdq.tx.toggle-inventory-visible/do!
                 :tx/show-message             cdq.tx.show-message/do!
                 :tx/show-modal               cdq.tx.show-modal/do!
                 :tx/set-item                 cdq.tx.set-item/do!
                 :tx/remove-item              cdq.tx.remove-item/do!
                 :tx/add-skill                cdq.tx.add-skill/do!
                 }
               requiring-resolve))

(def txs-fn-map
  (update-vals '{
                 ;; FIXME only this passes ctx, otherwise 'world' only
                 :tx/state-exit               cdq.world.tx.state-exit/do!
                 :tx/audiovisual              cdq.world.tx.audiovisual/do!
                 ;;

                 :tx/assoc                    cdq.world.tx.assoc/do!
                 :tx/assoc-in                 cdq.world.tx.assoc-in/do!
                 :tx/dissoc                   cdq.world.tx.dissoc/do!
                 :tx/update                   cdq.world.tx.update/do!
                 :tx/mark-destroyed           cdq.world.tx.mark-destroyed/do!
                 :tx/set-cooldown             cdq.world.tx.set-cooldown/do!
                 :tx/add-text-effect          cdq.world.tx.add-text-effect/do!
                 :tx/add-skill                cdq.world.tx.add-skill/do!
                 :tx/set-item                 cdq.world.tx.set-item/do!
                 :tx/remove-item              cdq.world.tx.remove-item/do!
                 :tx/pickup-item              cdq.world.tx.pickup-item/do!
                 :tx/event                    cdq.world.tx.event/do!
                 :tx/state-enter              cdq.world.tx.state-enter/do!
                 :tx/effect                   cdq.world.tx.effect/do!
                 :tx/spawn-alert              cdq.world.tx.spawn-alert/do!
                 :tx/spawn-line               cdq.world.tx.spawn-line/do!
                 :tx/move-entity              cdq.world.tx.move-entity/do!
                 :tx/spawn-projectile         cdq.world.tx.spawn-projectile/do!
                 :tx/spawn-effect             cdq.world.tx.spawn-effect/do!
                 :tx/spawn-item               cdq.world.tx.spawn-item/do!
                 :tx/spawn-creature           cdq.world.tx.spawn-creature/do!
                 :tx/spawn-entity             cdq.world.tx.spawn-entity/do!
                 :tx/sound                    cdq.world.tx.nothing/do!
                 :tx/toggle-inventory-visible cdq.world.tx.nothing/do!
                 :tx/show-message             cdq.world.tx.nothing/do!
                 :tx/show-modal               cdq.world.tx.nothing/do!
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
