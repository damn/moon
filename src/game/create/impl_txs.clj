(ns game.create.impl-txs
  (:require [moon.txs :as txs]))

; TODO tx-core/ & tx/
; core is those which return nil and actually _do_ something
; same with draws/
; TODO :tx/swap
;:tx/swap   (fn [_ctx eid & params]
;             (apply swap! eid params)
(def txs-fn-map
  (update-vals '{
                 :tx/state-exit               game.tx.state-exit/do!
                 :tx/audiovisual              game.tx.audiovisual/do!
                 :tx/assoc                    game.tx.assoc/do!
                 :tx/assoc-in                 game.tx.assoc-in/do!
                 :tx/dissoc                   game.tx.dissoc/do!
                 :tx/update                   game.tx.update/do!
                 :tx/mark-destroyed           game.tx.mark-destroyed/do!
                 :tx/set-cooldown             game.tx.set-cooldown/do!
                 :tx/add-text-effect          game.tx.add-text-effect/do!
                 :tx/add-skill                game.tx.add-skill/do!
                 :tx/set-item                 game.tx.set-item/do!
                 :tx/remove-item              game.tx.remove-item/do!
                 :tx/pickup-item              game.tx.pickup-item/do!
                 :tx/event                    game.tx.event/do!
                 :tx/register-eid             game.tx.register-eid/do!
                 :tx/unregister-eid           game.tx.unregister-eid/do!
                 :tx/state-enter              game.tx.state-enter/do!
                 :tx/effect                   game.tx.effect/do!
                 :tx/spawn-alert              game.tx.spawn-alert/do!
                 :tx/spawn-line               game.tx.spawn-line/do!
                 :tx/move-entity              game.tx.move-entity/do!
                 :tx/spawn-projectile         game.tx.spawn-projectile/do!
                 :tx/spawn-effect             game.tx.spawn-effect/do!
                 :tx/spawn-item               game.tx.spawn-item/do!
                 :tx/spawn-creature           game.tx.spawn-creature/do!
                 :tx/spawn-entity             game.tx.spawn-entity/do!
                 :tx/sound                    game.tx.nothing/do!
                 :tx/toggle-inventory-visible game.tx.nothing/do!
                 :tx/show-message             game.tx.nothing/do!
                 :tx/show-modal               game.tx.nothing/do!
                 }
               requiring-resolve))

(def reaction-txs-fn-map
  (update-vals '{
                 :tx/sound                    game.reaction-txs.sound/do!
                 :tx/toggle-inventory-visible game.reaction-txs.toggle-inventory-visible/do!
                 :tx/show-message             game.reaction-txs.show-message/do!
                 :tx/show-modal               game.reaction-txs.show-modal/do!
                 :tx/set-item                 game.reaction-txs.set-item/do!
                 :tx/remove-item              game.reaction-txs.remove-item/do!
                 :tx/add-skill                game.reaction-txs.add-skill/do!
                 }
               requiring-resolve))

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

            ; TODO VALID RETURNS -> check!
            ; either nil? or vector of transactions (vectors = vector with keyword & params?)
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

(defn step [ctx]
  (extend-type (class ctx)
    txs/Txs
    (handle! [ctx txs]
      (let [handled-txs (try (actions! txs-fn-map ctx txs)
                             (catch Throwable t
                               (throw (ex-info "Error handling txs"
                                               {:txs txs} t))))]
        (reduce-actions! reaction-txs-fn-map
                         ctx
                         handled-txs))))
  ctx)
