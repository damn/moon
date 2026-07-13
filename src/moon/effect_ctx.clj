(ns moon.effect-ctx
  "Accessors for effect-ctx maps — see docs/ctx-accessors.md.")

(defn get-source [effect-ctx]
  (:effect/source effect-ctx))

(defn get-target [effect-ctx]
  (:effect/target effect-ctx))

(defn get-target-position [effect-ctx]
  (:effect/target-position effect-ctx))

(defn get-target-direction [effect-ctx]
  (:effect/target-direction effect-ctx))

(defn without-target [effect-ctx]
  (dissoc effect-ctx :effect/target))
