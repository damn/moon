(ns clojure.gdx)

(defmulti object :type)

#_(defmethod gdx/object :gdx/sprite-batch [_options]
  (SpriteBatch.))
