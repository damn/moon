(ns moon.effect)

(defmulti applicable?
  (fn [[k _v] _effect-ctx]
    k))

(defmulti handle
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmulti useful?
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod useful? :default
  [_ _effect-ctx _ctx]
  true)

(defmulti render
  (fn [[k _v] _effect-ctx _ctx]
    k))

(defmethod render :default
  [_ _effect-ctx _ctx]
  nil)
