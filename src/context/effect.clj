(ns context.effect
  (:require [clojure.string :as str]
            [api.context :refer [transact-all! valid-params?]]
            [api.effect :as effect]
            [api.tx :refer [transact!]]))

(extend-type api.context.Context
  api.context/EffectInterpreter
  (effect-text [ctx txs]
    (->> (keep #(effect/text % ctx) txs)
         (str/join "\n")))

  (valid-params? [ctx txs]
    (every? #(effect/valid-params? % ctx) txs))

  (effect-useful? [ctx txs]
    (some #(effect/useful? % ctx) txs))

  (effect-render-info [ctx g txs]
    (doseq [tx txs]
      (effect/render-info tx g ctx))))

(defn- invalid-tx [ctx txs]
  (some #(when (not (effect/valid-params? % ctx)) %) txs))

(defmethod transact! :tx/effect [[_ effect-ctx txs] ctx]
  ; effect-ctx overwrites :game.context/uids-entities of new ctx !
  ; thats why merge effect-ctx ctx and not the other way around...
  ; make separate effect-ctx passing with the txs values, no need merge ! dont merge !
  ; => see @ effect/target-entity passing ctx to tx/effect ...
  (let [ctx (merge effect-ctx ctx)]
    (assert (valid-params? ctx txs) (pr-str (invalid-tx ctx txs)))
    (transact-all! ctx txs))
  ;[]
  )

(comment
 (let [effect [[:effect/melee-damage true]
               [:effect/sound "asd.wav"]]
       ectx {:effect/source :foo
             :effect/target :bar}]
   (for [[k & vs] effect]
     (apply vector k ectx vs))))
