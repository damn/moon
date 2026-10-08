(ns effect.useful
  (:require [moon.body :as body]
            [moon.v2 :as v2]))

(defn- double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]}
  (let [path-w (+ path-w 0.02)
        v (v2/direction [start-x start-y]
                        [target-y target-y])
        [normal1 normal2] (v2/normal-vectors v)
        normal1 (v2/scale normal1 (/ path-w 2))
        normal2 (v2/scale normal2 (/ path-w 2))
        start1  (v2/add [start-x  start-y]  normal1)
        start2  (v2/add [start-x  start-y]  normal2)
        target1 (v2/add [target-x target-y] normal1)
        target2 (v2/add [target-x target-y] normal2)]
    [start1 target1 start2 target2]))

(defn useful?
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
           (< (v2/distance source-p target-p)
              max-range)))

    :effects/target-all
    false

    :effects/target-entity
    (body/in-range? @(:effect/source effect-ctx)
                    @(:effect/target effect-ctx)
                    (:maxrange v))

    :effects.target/audiovisual
    false

    true))
