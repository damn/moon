(ns effect.applicable
  (:require [moon.faction :as faction]
            [moon.stats :as stats]))

(defn applicable?
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
         (seq (filter #(applicable? % effect-ctx) (:entity-effects v))))

    :effects.target/audiovisual
    (:effect/target effect-ctx)

    :effects.target/convert
    (let [source (:effect/source effect-ctx)
          target (:effect/target effect-ctx)]
      (and target
           (= (:entity/faction @target)
              (faction/enemy (:entity/faction @source)))))

    :effects.target/damage
    (and (:effect/target effect-ctx)
         #_(:stats/hp @target))

    :effects.target/kill
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))

    :effects.target/melee-damage
    (applicable? [:effects.target/damage (stats/melee-damage @(:effect/source effect-ctx))]
                 effect-ctx)

    :effects.target/spiderweb
    (:entity/stats @(:effect/target effect-ctx))

    :effects.target/stun
    (and (:effect/target effect-ctx)
         (:entity/fsm @(:effect/target effect-ctx)))))
