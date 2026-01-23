(ns moon.utils
  (:require [clojure.math :as math]))

(defn- index-of [k ^clojure.lang.PersistentVector v]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))

(comment

 ; simpler way to do 'sort-by-k-order':

 (index-of :foo [:a :b :foo :c])
 (contains? [:a :b :foo :c] :foo)
 (def order [:low :medium :high])
 (def items [:high :low :medium :low :high])

 ;; build order lookup map
 (def order-map (zipmap order (range)))

 (sort-by order-map items)
 ;; => (:low :low :medium :high :high)
 )

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]] (or (index-of k k-order) max-count))
             components)))

(defn define-order [order-k-vector]
  (apply hash-map (interleave order-k-vector (range))))

(defn sort-by-order [coll get-item-order-k order]
  (sort-by #((get-item-order-k %) order) < coll))

(defn safe-merge [m1 m2]
  {:pre [(not-any? #(contains? m1 %) (keys m2))]}
  (merge m1 m2))

(def float-rounding-error (double 0.000001)) ; FIXME clojure uses doubles?

(defn nearly-equal? [^double x ^double y]
  (<= (Math/abs (- x y)) float-rounding-error))

(defn- approx-numbers [a b epsilon]
  (<= (Math/abs (double (- a b))) epsilon))

(defn- round-n-decimals [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))

(defn readable-number [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (approx-numbers x (int x) 0.001))
    (int x)
    (round-n-decimals x 2)))

(defn- indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
  is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll) :when (pred elt)] idx))

(defmacro when-seq [[aseq bind] & body]
  `(let [~aseq ~bind]
     (when (seq ~aseq)
       ~@body)))

(defn dissoc-in [m ks]
  (assert (> (count ks) 1))
  (update-in m (drop-last ks) dissoc (last ks)))

(defn ->edn-str ^String [v]
  (binding [*print-level* nil]
    (pr-str v)))

(defn truncate [s limit]
  (if (> (count s) limit)
    (str (subs s 0 limit) "...")
    s))

(defn apply-kvs [m f]
  (reduce (fn [m k]
            (assoc m k (f k (get m k))))
          m
          (keys m)))

(defn recur-sort-map [m]
  (into (sorted-map)
        (zipmap (keys m)
                (map #(if (map? %)
                        (recur-sort-map %)
                        %)
                     (vals m)))))
