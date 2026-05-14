(ns moon.application.create.impl-txs
  (:require [moon.txs :as txs]))

; TODO tx-core/ & tx/
; core is those which return nil and actually _do_ something
; same with draws/
; TODO :tx/swap
;:tx/swap   (fn [_ctx eid & params]
;             (apply swap! eid params)
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
